
package com.auction.tournament.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.auction.exception.BusinessException;
import com.auction.exception.ResourceNotFoundException;

import com.auction.storage.enums.ImageCategory;
import com.auction.storage.service.ImageStorageService;

import com.auction.tournament.dto.TournamentCreateRequest;
import com.auction.tournament.dto.TournamentResponse;
import com.auction.tournament.dto.TournamentUpdateRequest;
import com.auction.tournament.entity.Tournament;
import com.auction.tournament.enums.TournamentStatus;
import com.auction.tournament.mapper.TournamentMapper;
import com.auction.tournament.repository.TournamentRepository;

@Service
@Transactional
public class TournamentServiceImpl implements TournamentService {

    private static final Logger log =
            LoggerFactory.getLogger(TournamentServiceImpl.class);

    private final TournamentRepository tournamentRepository;

    private final TournamentMapper tournamentMapper;

    private final ImageStorageService imageStorageService;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public TournamentServiceImpl(
            TournamentMapper tournamentMapper,
            TournamentRepository tournamentRepository,
            ImageStorageService imageStorageService) {

        this.tournamentMapper = tournamentMapper;
        this.tournamentRepository = tournamentRepository;
        this.imageStorageService = imageStorageService;
    }

    // ==========================================
    // CREATE TOURNAMENT
    // ==========================================

    @Override
    public TournamentResponse createTournament(
            TournamentCreateRequest request) {

        if (tournamentRepository.existsByNameIgnoreCase(
                request.getName())) {

            throw new BusinessException(
                    "Tournament with this name already exists");
        }

        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getEndDate().isBefore(
                        request.getStartDate())) {

            throw new BusinessException(
                    "End date cannot be before start date");
        }

        Tournament tournament =
                tournamentMapper.toEntity(request);

        tournament.setStatus(TournamentStatus.DRAFT);

        Tournament savedTournament =
                tournamentRepository.save(tournament);

        return tournamentMapper.toResponse(savedTournament);
    }

    // ==========================================
    // GET TOURNAMENT BY ID
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public TournamentResponse getTournament(UUID id) {

        Tournament tournament = findTournamentById(id);

        return tournamentMapper.toResponse(tournament);
    }

    // ==========================================
    // GET ALL TOURNAMENTS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<TournamentResponse> getAllTournaments() {

        return tournamentRepository.findAll()
                .stream()
                .map(tournamentMapper::toResponse)
                .toList();
    }

    // ==========================================
    // UPDATE TOURNAMENT
    // ==========================================

    @Override
    public TournamentResponse updateTournament(
            UUID id,
            TournamentUpdateRequest request) {

        Tournament tournament = findTournamentById(id);

        if (tournament.getStatus() != TournamentStatus.DRAFT) {

            throw new BusinessException(
                    "Only tournaments in DRAFT status can be updated");
        }

        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getEndDate().isBefore(
                        request.getStartDate())) {

            throw new BusinessException(
                    "End date cannot be before start date");
        }

        tournamentMapper.updateEntity(request, tournament);

        Tournament updatedTournament =
                tournamentRepository.save(tournament);

        return tournamentMapper.toResponse(updatedTournament);
    }

    // ==========================================
    // DELETE TOURNAMENT
    // ==========================================

    @Override
    public void deleteTournament(UUID id) {

        Tournament tournament = findTournamentById(id);

        if (tournament.getStatus() != TournamentStatus.DRAFT) {

            throw new BusinessException(
                    "Only DRAFT tournaments can be deleted");
        }

        String existingLogoPath = tournament.getLogoUrl();

        // Delete the image only after the database
        // transaction has committed successfully.

        registerLogoCleanup(
                id,
                existingLogoPath,
                null
        );

        tournamentRepository.delete(tournament);
    }

    // ==========================================
    // OPEN REGISTRATION
    // ==========================================

    @Override
    public TournamentResponse openRegistration(
            UUID tournamentId) {

        Tournament tournament =
                findTournamentById(tournamentId);

        if (tournament.getStatus() != TournamentStatus.DRAFT) {

            throw new BusinessException(
                    "Registration can only be opened "
                    + "for a DRAFT tournament");
        }

        tournament.setStatus(
                TournamentStatus.REGISTRATION_OPEN);

        return tournamentMapper.toResponse(
                tournamentRepository.save(tournament));
    }

    // ==========================================
    // CLOSE REGISTRATION
    // ==========================================

    @Override
    public TournamentResponse closeRegistration(
            UUID tournamentId) {

        Tournament tournament =
                findTournamentById(tournamentId);

        if (tournament.getStatus()
                != TournamentStatus.REGISTRATION_OPEN) {

            throw new BusinessException(
                    "Registration can only be closed when "
                    + "the tournament is REGISTRATION_OPEN");
        }

        tournament.setStatus(
                TournamentStatus.REGISTRATION_CLOSED);

        return tournamentMapper.toResponse(
                tournamentRepository.save(tournament));
    }

    // ==========================================
    // PREPARE AUCTION
    // ==========================================

    @Override
    public TournamentResponse prepareAuction(
            UUID tournamentId) {

        Tournament tournament =
                findTournamentById(tournamentId);

        if (tournament.getStatus()
                != TournamentStatus.REGISTRATION_CLOSED) {

            throw new BusinessException(
                    "Auction preparation requires "
                    + "REGISTRATION_CLOSED status");
        }

        tournament.setStatus(
                TournamentStatus.AUCTION_PENDING);

        return tournamentMapper.toResponse(
                tournamentRepository.save(tournament));
    }

    // ==========================================
    // UPLOAD OR REPLACE TOURNAMENT LOGO
    // ==========================================

    @Override
    public TournamentResponse uploadLogo(
            UUID tournamentId,
            MultipartFile file) {

        log.info(
                "Tournament logo upload requested. TournamentId={}",
                tournamentId
        );

        Tournament tournament =
                findTournamentById(tournamentId);

        validateActiveTransaction();

        String existingLogoPath =
                tournament.getLogoUrl();

        String newLogoPath;

        // STEP 1: Validate and upload the new image.

        try {

            newLogoPath = imageStorageService.uploadImage(
                    file,
                    ImageCategory.TOURNAMENT,
                    tournamentId
            );

        } catch (IllegalArgumentException ex) {

            throw new BusinessException(ex.getMessage());
        }

        // STEP 2: Register transaction-aware cleanup.
        // If registration fails, remove the newly uploaded image.

        try {

            registerLogoCleanup(
                    tournamentId,
                    existingLogoPath,
                    newLogoPath
            );

        } catch (RuntimeException ex) {

            deleteImageSafely(
                    tournamentId,
                    newLogoPath
            );

            throw ex;
        }

        // STEP 3: Update the tournament logo reference.

        tournament.setLogoUrl(newLogoPath);

        // STEP 4: Save and flush the database changes.

        Tournament updatedTournament =
                tournamentRepository.saveAndFlush(tournament);

        log.debug(
                "Tournament logo reference updated. "
                + "TournamentId={}, LogoPath={}",
                tournamentId,
                newLogoPath
        );

        // STEP 5: Return the updated tournament response.
        // File cleanup is handled after transaction completion.

        return tournamentMapper.toResponse(updatedTournament);
    }

    // ==========================================
    // DELETE TOURNAMENT LOGO
    // ==========================================

    @Override
    public TournamentResponse deleteLogo(
            UUID tournamentId) {

        log.info(
                "Tournament logo deletion requested. TournamentId={}",
                tournamentId
        );

        Tournament tournament =
                findTournamentById(tournamentId);

        String existingLogoPath =
                tournament.getLogoUrl();

        // No logo is currently associated with the tournament.

        if (existingLogoPath == null
                || existingLogoPath.isBlank()) {

            log.debug(
                    "Tournament has no logo to delete. TournamentId={}",
                    tournamentId
            );

            return tournamentMapper.toResponse(tournament);
        }

        validateActiveTransaction();

        // STEP 1: Register old image cleanup after commit.

        registerLogoCleanup(
                tournamentId,
                existingLogoPath,
                null
        );

        // STEP 2: Clear the database reference.

        tournament.setLogoUrl(null);

        // STEP 3: Save and flush database changes.

        Tournament updatedTournament =
                tournamentRepository.saveAndFlush(tournament);

        log.debug(
                "Tournament logo reference cleared. TournamentId={}",
                tournamentId
        );

        return tournamentMapper.toResponse(updatedTournament);
    }

    // ==========================================
    // REGISTER TRANSACTION-AWARE LOGO CLEANUP
    // ==========================================

    private void registerLogoCleanup(
            UUID tournamentId,
            String existingLogoPath,
            String newLogoPath) {

        boolean hasExistingLogo =
                existingLogoPath != null
                && !existingLogoPath.isBlank();

        boolean hasNewLogo =
                newLogoPath != null
                && !newLogoPath.isBlank();

        if (!hasExistingLogo && !hasNewLogo) {

            return;
        }

        validateActiveTransaction();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {

                        // CASE 1: DATABASE TRANSACTION COMMITTED
                        // Remove the previous logo.

                        if (status == STATUS_COMMITTED) {

                            if (hasExistingLogo
                                    && !existingLogoPath.equals(
                                            newLogoPath)) {

                                deleteImageSafely(
                                        tournamentId,
                                        existingLogoPath
                                );
                            }

                            log.debug(
                                    "Tournament logo transaction committed. "
                                    + "TournamentId={}",
                                    tournamentId
                            );

                        // CASE 2: DATABASE TRANSACTION ROLLED BACK
                        // Remove the newly uploaded image.
                        // Preserve the previous logo.

                        } else if (status == STATUS_ROLLED_BACK) {

                            if (hasNewLogo) {

                                deleteImageSafely(
                                        tournamentId,
                                        newLogoPath
                                );
                            }

                            log.warn(
                                    "Tournament logo transaction rolled back. "
                                    + "TournamentId={}",
                                    tournamentId
                            );

                        // CASE 3: TRANSACTION STATUS UNKNOWN
                        // Preserve both files to avoid data loss.

                        } else {

                            log.warn(
                                    "Tournament logo transaction status unknown. "
                                    + "Files retained for reconciliation. "
                                    + "TournamentId={}, OldPath={}, NewPath={}",
                                    tournamentId,
                                    existingLogoPath,
                                    newLogoPath
                            );
                        }
                    }
                }
        );
    }

    // ==========================================
    // VALIDATE ACTIVE TRANSACTION
    // ==========================================

    private void validateActiveTransaction() {

        if (!TransactionSynchronizationManager
                    .isActualTransactionActive()

                || !TransactionSynchronizationManager
                    .isSynchronizationActive()) {

            throw new IllegalStateException(
                    "An active transaction is required "
                    + "for tournament logo management."
            );
        }
    }

    // ==========================================
    // SAFELY DELETE TOURNAMENT IMAGE
    // ==========================================

    private void deleteImageSafely(
            UUID tournamentId,
            String imagePath) {

        if (imagePath == null || imagePath.isBlank()) {

            return;
        }

        // Ensure the image belongs to this tournament.

        String expectedPrefix =
                ImageCategory.TOURNAMENT.getDirectoryName()
                + "/"
                + tournamentId
                + "/";

        if (!imagePath.startsWith(expectedPrefix)) {

            log.warn(
                    "Skipping image deletion because the path "
                    + "does not belong to the tournament. "
                    + "TournamentId={}, ImagePath={}",
                    tournamentId,
                    imagePath
            );

            return;
        }

        try {

            imageStorageService.deleteImage(imagePath);

            log.debug(
                    "Tournament image cleanup completed. "
                    + "TournamentId={}, ImagePath={}",
                    tournamentId,
                    imagePath
            );

        } catch (RuntimeException ex) {

            // Cleanup failure must not invalidate
            // an already committed database transaction.

            log.error(
                    "Tournament image cleanup failed. "
                    + "Manual cleanup may be required. "
                    + "TournamentId={}, ImagePath={}",
                    tournamentId,
                    imagePath,
                    ex
            );
        }
    }

    // ==========================================
    // FIND TOURNAMENT BY ID
    // ==========================================

    private Tournament findTournamentById(UUID id) {

        return tournamentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tournament not found with id: " + id));
    }
}
