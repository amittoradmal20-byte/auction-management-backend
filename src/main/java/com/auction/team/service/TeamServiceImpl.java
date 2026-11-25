
package com.auction.team.service;

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
import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.storage.enums.ImageCategory;
import com.auction.storage.service.ImageStorageService;
import com.auction.team.dto.TeamCreateRequest;
import com.auction.team.dto.TeamResponse;
import com.auction.team.dto.TeamUpdateRequest;
import com.auction.team.entity.Team;
import com.auction.team.enums.TeamStatus;
import com.auction.team.mapper.TeamMapper;
import com.auction.team.repository.TeamRepository;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private static final Logger log =
            LoggerFactory.getLogger(TeamServiceImpl.class);

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final ImageStorageService imageStorageService;

    public TeamServiceImpl(
            TeamRepository teamRepository,
            TeamMapper teamMapper,
            ImageStorageService imageStorageService) {

        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.imageStorageService = imageStorageService;
    }

    // ==========================================
    // CREATE TEAM
    // ==========================================

    @Override
    public TeamResponse createTeam(
            TeamCreateRequest request) {

        String name = request.getName().trim();
        String shortName = request.getShortName().trim();

        // 1. Check duplicate team name
        if (teamRepository.existsByNameIgnoreCase(name)) {

            throw new DuplicateResourceException(
                    "Team name already exists");
        }

        // 2. Check duplicate short name
        if (teamRepository.existsByShortNameIgnoreCase(shortName)) {

            throw new DuplicateResourceException(
                    "Team short name already exists");
        }

        // 3. Create master Team
        Team team = new Team();

        team.setName(name);
        team.setShortName(shortName);

        // Logo is managed through dedicated logo APIs
        team.setLogoUrl(null);

        // Server-controlled field
        team.setStatus(TeamStatus.ACTIVE);

        // 4. Save
        Team savedTeam =
                teamRepository.save(team);

        return teamMapper.toResponse(savedTeam);
    }

    // ==========================================
    // GET TEAM BY ID
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeam(UUID id) {

        Team team =
                teamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found"));

        return teamMapper.toResponse(team);
    }

    // ==========================================
    // GET ALL TEAMS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {

        return teamRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    // ==========================================
    // UPDATE TEAM
    // ==========================================

    @Override
    public TeamResponse updateTeam(
            UUID id,
            TeamUpdateRequest request) {

        Team team =
                teamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found"));

        String name = request.getName().trim();
        String shortName = request.getShortName().trim();

        // 1. Check duplicate name only if changed
        if (!team.getName().equalsIgnoreCase(name)
                && teamRepository.existsByNameIgnoreCase(name)) {

            throw new DuplicateResourceException(
                    "Team name already exists");
        }

        // 2. Check duplicate short name only if changed
        if (!team.getShortName().equalsIgnoreCase(shortName)
                && teamRepository.existsByShortNameIgnoreCase(shortName)) {

            throw new DuplicateResourceException(
                    "Team short name already exists");
        }

        // 3. Update editable fields
        // Existing logoUrl is protected by TeamMapper
        teamMapper.updateEntity(request, team);

        // 4. Save
        Team updatedTeam =
                teamRepository.save(team);

        return teamMapper.toResponse(updatedTeam);
    }

    // ==========================================
    // DELETE TEAM
    // ==========================================

    @Override
    public void deleteTeam(UUID id) {

        Team team =
                teamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found"));

        String existingLogoPath = team.getLogoUrl();

        // Delete associated logo only after
        // successful database transaction commit
        registerLogoCleanup(
                id,
                existingLogoPath,
                null);

        teamRepository.delete(team);
    }

    // ==========================================
    // UPLOAD OR REPLACE TEAM LOGO
    // ==========================================

    @Override
    public TeamResponse uploadLogo(
            UUID teamId,
            MultipartFile file) {

        log.info(
                "Uploading logo for team: {}",
                teamId);

        // 1. Find team
        Team team =
                teamRepository.findById(teamId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found with id: "
                                                + teamId));

        // 2. Verify transaction is active
        validateActiveTransaction();

        // 3. Preserve existing logo path
        String existingLogoPath = team.getLogoUrl();

        // 4. Upload new logo
        String newLogoPath;

        try {

            newLogoPath =
                    imageStorageService.uploadImage(
                            file,
                            ImageCategory.TEAM,
                            teamId);

        } catch (IllegalArgumentException ex) {

            throw new BusinessException(
                    ex.getMessage());
        }

        // 5. Register transaction-aware cleanup
        try {

            registerLogoCleanup(
                    teamId,
                    existingLogoPath,
                    newLogoPath);

        } catch (RuntimeException ex) {

            // Cleanup newly uploaded image if
            // transaction synchronization registration fails
            deleteImageSafely(
                    teamId,
                    newLogoPath);

            throw ex;
        }

        // 6. Update database reference
        team.setLogoUrl(newLogoPath);

        // 7. Save and flush
        Team updatedTeam =
                teamRepository.saveAndFlush(team);

        log.debug(
                "Team logo reference updated. Team ID: {}, Logo: {}",
                teamId,
                newLogoPath);

        // 8. Return updated response
        return teamMapper.toResponse(updatedTeam);
    }

    // ==========================================
    // DELETE TEAM LOGO
    // ==========================================

    @Override
    public TeamResponse deleteLogo(UUID teamId) {

        log.info(
                "Deleting logo for team: {}",
                teamId);

        // 1. Find team
        Team team =
                teamRepository.findById(teamId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found with id: "
                                                + teamId));

        // 2. Get existing logo
        String existingLogoPath = team.getLogoUrl();

        // 3. Return existing response if no logo exists
        if (existingLogoPath == null
                || existingLogoPath.isBlank()) {

            log.debug(
                    "No logo available for team: {}",
                    teamId);

            return teamMapper.toResponse(team);
        }

        // 4. Verify active transaction
        validateActiveTransaction();

        // 5. Register physical image cleanup
        registerLogoCleanup(
                teamId,
                existingLogoPath,
                null);

        // 6. Clear database reference
        team.setLogoUrl(null);

        // 7. Save and flush
        Team updatedTeam =
                teamRepository.saveAndFlush(team);

        log.debug(
                "Team logo reference cleared. Team ID: {}",
                teamId);

        return teamMapper.toResponse(updatedTeam);
    }

    // ==========================================
    // TRANSACTION-AWARE LOGO CLEANUP
    // ==========================================

    private void registerLogoCleanup(
            UUID teamId,
            String oldLogoPath,
            String newLogoPath) {

        boolean hasOldLogo =
                oldLogoPath != null
                        && !oldLogoPath.isBlank();

        boolean hasNewLogo =
                newLogoPath != null
                        && !newLogoPath.isBlank();

        // Nothing to clean up
        if (!hasOldLogo && !hasNewLogo) {
            return;
        }

        validateActiveTransaction();

        TransactionSynchronizationManager
                .registerSynchronization(
                        new TransactionSynchronization() {

                            @Override
                            public void afterCompletion(int status) {

                                // ==============================
                                // TRANSACTION COMMITTED
                                // ==============================

                                if (status == STATUS_COMMITTED) {

                                    if (hasOldLogo
                                            && !oldLogoPath.equals(
                                                    newLogoPath)) {

                                        deleteImageSafely(
                                                teamId,
                                                oldLogoPath);
                                    }

                                // ==============================
                                // TRANSACTION ROLLED BACK
                                // ==============================

                                } else if (status == STATUS_ROLLED_BACK) {

                                    if (hasNewLogo) {

                                        deleteImageSafely(
                                                teamId,
                                                newLogoPath);
                                    }

                                // ==============================
                                // UNKNOWN TRANSACTION STATUS
                                // ==============================

                                } else {

                                    log.warn(
                                            "Unknown transaction status "
                                            + "during team logo cleanup. "
                                            + "Team ID: {}, Status: {}",
                                            teamId,
                                            status);
                                }
                            }
                        });
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
                    + "for team logo management.");
        }
    }

    // ==========================================
    // SAFE PHYSICAL IMAGE DELETION
    // ==========================================

    private void deleteImageSafely(
            UUID teamId,
            String imagePath) {

        if (imagePath == null
                || imagePath.isBlank()) {

            return;
        }

        // Verify image belongs to the correct team
        String expectedPrefix =
                ImageCategory.TEAM.getDirectoryName()
                        + "/"
                        + teamId
                        + "/";

        if (!imagePath.startsWith(expectedPrefix)) {

            log.warn(
                    "Skipping image deletion because "
                    + "the path does not belong to the team. "
                    + "Team ID: {}, Image path: {}",
                    teamId,
                    imagePath);

            return;
        }

        try {

            imageStorageService.deleteImage(imagePath);

            log.debug(
                    "Team image cleanup completed. "
                    + "Team ID: {}, Image path: {}",
                    teamId,
                    imagePath);

        } catch (RuntimeException ex) {

            log.error(
                    "Failed to delete team image. "
                    + "Team ID: {}, Image path: {}",
                    teamId,
                    imagePath,
                    ex);
        }
    }
}
