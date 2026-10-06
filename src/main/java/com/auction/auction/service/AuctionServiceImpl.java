package com.auction.auction.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auction.dto.AuctionCreateRequest;
import com.auction.auction.dto.AuctionResponse;
import com.auction.auction.dto.AuctionUpdateRequest;
import com.auction.auction.entity.Auction;
import com.auction.auction.enums.AuctionStatus;
import com.auction.auction.mapper.AuctionMapper;
import com.auction.auction.repository.AuctionRepository;
import com.auction.exception.BusinessException;
import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.tournament.entity.Tournament;
import com.auction.tournament.enums.TournamentStatus;
import com.auction.tournament.repository.TournamentRepository;


@Service
@Transactional
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;
    private final TournamentRepository tournamentRepository;
    
	public AuctionServiceImpl(AuctionRepository auctionRepository, AuctionMapper auctionMapper, TournamentRepository tournamentRepository) {
		this.auctionRepository = auctionRepository;
		this.auctionMapper = auctionMapper;
		this.tournamentRepository = tournamentRepository;
	}


    @Override
    public AuctionResponse createAuction(AuctionCreateRequest request) {

        Tournament tournament = tournamentRepository
                .findById(request.getTournamentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tournament not found"));

        if (tournament.getStatus() != TournamentStatus.DRAFT) {
            throw new BusinessException(
                    "Auction can only be created for a tournament in DRAFT status");
        }

        List<AuctionStatus> activeStatuses = List.of(
                AuctionStatus.SCHEDULED,
                AuctionStatus.IN_PROGRESS,
                AuctionStatus.PAUSED);

        if (auctionRepository.existsByTournamentIdAndStatusIn(
                request.getTournamentId(),
                activeStatuses)) {

            throw new DuplicateResourceException(
                    "An active auction already exists for this tournament");
        }

        validateAuctionTimes(
                request.getStartTime(),
                request.getEndTime());

        Auction auction = auctionMapper.toEntity(request);

        auction.setTournament(tournament);
        auction.setStatus(AuctionStatus.DRAFT);

        Auction savedAuction = auctionRepository.save(auction);

        return auctionMapper.toResponse(savedAuction);
    }

    @Override
    @Transactional(readOnly = true)
    public AuctionResponse getAuction(UUID auctionId) {

        Auction auction = findAuctionById(auctionId);

        return auctionMapper.toResponse(auction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionResponse> getAllAuctions() {

        return auctionRepository.findAll()
                .stream()
                .map(auctionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionResponse> getAuctionsByTournament(
            UUID tournamentId) {

        if (!tournamentRepository.existsById(tournamentId)) {
            throw new ResourceNotFoundException(
                    "Tournament not found");
        }

        return auctionRepository.findByTournamentId(tournamentId)
                .stream()
                .map(auctionMapper::toResponse)
                .toList();
    }

    @Override
    public AuctionResponse updateAuction(
            UUID auctionId,
            AuctionUpdateRequest request) {

        Auction auction = findAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.DRAFT) {
            throw new BusinessException(
                    "Only a DRAFT auction can be updated");
        }

        validateAuctionTimes(
                request.getStartTime(),
                request.getEndTime());

        auctionMapper.updateEntity(request, auction);

        Auction updatedAuction = auctionRepository.save(auction);

        return auctionMapper.toResponse(updatedAuction);
    }

    @Override
    public void deleteAuction(UUID auctionId) {

        Auction auction = findAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.DRAFT) {
            throw new BusinessException(
                    "Only a DRAFT auction can be deleted");
        }

        auctionRepository.delete(auction);
    }

    private Auction findAuctionById(UUID auctionId) {

        return auctionRepository.findById(auctionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Auction not found"));
    }

    private void validateAuctionTimes(
            LocalDateTime startTime,
            LocalDateTime endTime) {

        if (startTime != null &&
                endTime != null &&
                !endTime.isAfter(startTime)) {

            throw new BusinessException(
                    "Auction end time must be after start time");
        }
    }
    
    @Override
    public AuctionResponse scheduleAuction(UUID auctionId) {

        Auction auction = findAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.DRAFT) {
            throw new BusinessException(
                    "Only a DRAFT auction can be scheduled");
        }

        auction.setStatus(AuctionStatus.SCHEDULED);

        return auctionMapper.toResponse(auctionRepository.save(auction));
    }
    
    @Override
    public AuctionResponse startAuction(UUID auctionId) {

        Auction auction = findAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.SCHEDULED) {
            throw new BusinessException(
                    "Only a SCHEDULED auction can be started");
        }

        auction.setStatus(AuctionStatus.IN_PROGRESS);

        return auctionMapper.toResponse(auctionRepository.save(auction));
    }
    
    @Override
    public AuctionResponse pauseAuction(UUID auctionId) {

        Auction auction = findAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new BusinessException(
                    "Only an IN_PROGRESS auction can be paused");
        }

        auction.setStatus(AuctionStatus.PAUSED);

        return auctionMapper.toResponse(auctionRepository.save(auction));
    }
    
    @Override
    public AuctionResponse resumeAuction(UUID auctionId) {

        Auction auction = findAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.PAUSED) {
            throw new BusinessException(
                    "Only a PAUSED auction can be resumed");
        }

        auction.setStatus(AuctionStatus.IN_PROGRESS);

        return auctionMapper.toResponse(auctionRepository.save(auction));
    }
    
    @Override
    public AuctionResponse completeAuction(UUID auctionId) {

        Auction auction = findAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new BusinessException(
                    "Only an IN_PROGRESS auction can be completed");
        }

        auction.setStatus(AuctionStatus.COMPLETED);

        return auctionMapper.toResponse(auctionRepository.save(auction));
    }
    
    @Override
    public AuctionResponse cancelAuction(UUID auctionId) {

        Auction auction = findAuctionById(auctionId);

        if (auction.getStatus() != AuctionStatus.DRAFT &&
                auction.getStatus() != AuctionStatus.SCHEDULED) {

            throw new BusinessException(
                    "Only a DRAFT or SCHEDULED auction can be cancelled");
        }

        auction.setStatus(AuctionStatus.CANCELLED);

        return auctionMapper.toResponse(auctionRepository.save(auction));
    }
    
    
}