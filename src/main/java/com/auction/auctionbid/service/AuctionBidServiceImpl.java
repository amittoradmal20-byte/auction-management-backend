package com.auction.auctionbid.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auction.entity.Auction;
import com.auction.auction.enums.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.auctionbid.dto.AuctionBidCreateRequest;
import com.auction.auctionbid.dto.AuctionBidResponse;
import com.auction.auctionbid.entity.AuctionBid;
import com.auction.auctionbid.repository.AuctionBidRepository;
import com.auction.auctionplayer.entity.AuctionPlayer;
import com.auction.auctionplayer.enums.AuctionPlayerStatus;
import com.auction.auctionplayer.repository.AuctionPlayerRepository;
import com.auction.exception.BusinessException;
import com.auction.tournamentteam.entity.TournamentTeam;
import com.auction.tournamentteam.enums.TournamentTeamStatus;
import com.auction.tournamentteam.repository.TournamentTeamRepository;

@Service
public class AuctionBidServiceImpl implements AuctionBidService {

    private static final int MAX_SQUAD_SIZE = 15;

    private static final BigDecimal MINIMUM_RESERVE_PER_SLOT =
            new BigDecimal("1000.00");

    private final AuctionBidRepository auctionBidRepository;
    private final AuctionPlayerRepository auctionPlayerRepository;
    private final AuctionRepository auctionRepository;
    private final TournamentTeamRepository tournamentTeamRepository;

    public AuctionBidServiceImpl(
            AuctionBidRepository auctionBidRepository,
            AuctionPlayerRepository auctionPlayerRepository,
            AuctionRepository auctionRepository,
            TournamentTeamRepository tournamentTeamRepository) {

        this.auctionBidRepository = auctionBidRepository;
        this.auctionPlayerRepository = auctionPlayerRepository;
        this.auctionRepository = auctionRepository;
        this.tournamentTeamRepository = tournamentTeamRepository;
    }

    @Override
    @Transactional
    public AuctionBidResponse placeBid(
            UUID auctionPlayerId,
            AuctionBidCreateRequest request) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository
                .findById(auctionPlayerId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Auction player not found"));

        Auction auction = auctionRepository
                .findById(auctionPlayer.getAuction().getId())
                .orElseThrow(() ->
                        new BusinessException(
                                "Auction not found"));

        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new BusinessException(
                    "Bids can only be placed while the auction is in progress");
        }

        if (auctionPlayer.getAuctionStatus()
                != AuctionPlayerStatus.IN_AUCTION) {
            throw new BusinessException(
                    "This player is not currently open for bidding");
        }

        TournamentTeam team = tournamentTeamRepository
                .findById(request.getTournamentTeamId())
                .orElseThrow(() ->
                        new BusinessException(
                                "Tournament team not found"));

        if (team.getStatus() != TournamentTeamStatus.ACTIVE) {
            throw new BusinessException(
                    "Only active tournament teams can place bids");
        }

        if (!team.getTournament().getId()
                .equals(auction.getTournament().getId())) {
            throw new BusinessException(
                    "The team does not belong to this auction's tournament");
        }

        long purchasedPlayers =
                auctionPlayerRepository.countSoldPlayersByTeamAndTournament(
                        team.getId(),
                        auction.getTournament().getId(),
                        AuctionPlayerStatus.SOLD);

        if (purchasedPlayers >= MAX_SQUAD_SIZE) {
            throw new BusinessException(
                    "The team has already reached its 15-player squad limit");
        }

        int remainingSlots =
                MAX_SQUAD_SIZE - (int) purchasedPlayers;

        BigDecimal remainingBudget = team.getRemainingBudget();

        if (remainingBudget == null) {
            remainingBudget = BigDecimal.ZERO;
        }

        BigDecimal reserveForFutureSlots =
                MINIMUM_RESERVE_PER_SLOT.multiply(
                        BigDecimal.valueOf(remainingSlots - 1L));

        BigDecimal maximumAffordableBid =
                remainingBudget.subtract(reserveForFutureSlots);

        if (maximumAffordableBid.compareTo(BigDecimal.ZERO) < 0) {
            maximumAffordableBid = BigDecimal.ZERO;
        }

        if (maximumAffordableBid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "The team cannot afford another player while preserving "
                    + "the minimum reserve for its remaining squad slots");
        }

        BigDecimal highestBidAmount =
                auctionBidRepository
                        .findFirstByAuctionPlayerIdOrderByBidAmountDescBidTimeAsc(
                                auctionPlayerId)
                        .map(AuctionBid::getBidAmount)
                        .orElse(null);

        BigDecimal minimumAllowedBid;

        if (highestBidAmount == null) {
            minimumAllowedBid = auction.getOpeningBid();

            if (auctionPlayer.getBasePrice()
                    .compareTo(minimumAllowedBid) > 0) {
                minimumAllowedBid = auctionPlayer.getBasePrice();
            }
        } else {
            minimumAllowedBid =
                    highestBidAmount.add(auction.getBidIncrement());
        }

        BigDecimal bidAmount = request.getBidAmount();

        if (bidAmount == null
                || bidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "Bid amount must be greater than zero");
        }

        if (bidAmount.compareTo(minimumAllowedBid) < 0) {
            throw new BusinessException(
                    "Bid must be at least " + minimumAllowedBid);
        }

        if (bidAmount.compareTo(maximumAffordableBid) > 0) {
            throw new BusinessException(
                    "Bid exceeds the team's maximum affordable bid of "
                    + maximumAffordableBid);
        }

        AuctionBid bid = new AuctionBid();
        bid.setAuctionPlayer(auctionPlayer);
        bid.setTournamentTeam(team);
        bid.setBidAmount(bidAmount);
        bid.setBidTime(LocalDateTime.now());

        AuctionBid savedBid = auctionBidRepository.save(bid);

        auctionPlayer.setCurrentBid(bidAmount);
        auctionPlayerRepository.save(auctionPlayer);

        return toResponse(savedBid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionBidResponse> getBidHistory(
            UUID auctionPlayerId) {

        if (!auctionPlayerRepository.existsById(auctionPlayerId)) {
            throw new BusinessException(
                    "Auction player not found");
        }

        return auctionBidRepository
                .findByAuctionPlayerIdOrderByBidTimeDesc(auctionPlayerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuctionBidResponse getHighestBid(UUID auctionPlayerId) {

        if (!auctionPlayerRepository.existsById(auctionPlayerId)) {
            throw new BusinessException(
                    "Auction player not found");
        }

        AuctionBid highestBid = auctionBidRepository
                .findFirstByAuctionPlayerIdOrderByBidAmountDescBidTimeAsc(
                        auctionPlayerId)
                .orElseThrow(() ->
                        new BusinessException(
                                "No bids have been placed for this player"));

        return toResponse(highestBid);
    }

    private AuctionBidResponse toResponse(AuctionBid bid) {

        AuctionBidResponse response = new AuctionBidResponse();

        response.setId(bid.getId());
        response.setAuctionPlayerId(
                bid.getAuctionPlayer().getId());
        response.setTournamentTeamId(
                bid.getTournamentTeam().getId());
        response.setBidAmount(bid.getBidAmount());
        response.setBidTime(bid.getBidTime());

        return response;
    }
}
