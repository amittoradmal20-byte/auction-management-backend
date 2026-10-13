package com.auction.auctionplayer.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auction.entity.Auction;
import com.auction.auction.enums.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.auctionbid.entity.AuctionBid;
import com.auction.auctionbid.repository.AuctionBidRepository;
import com.auction.auctionplayer.dto.AuctionPlayerCreateRequest;
import com.auction.auctionplayer.dto.AuctionPlayerResponse;
import com.auction.auctionplayer.entity.AuctionPlayer;
import com.auction.auctionplayer.enums.AuctionPlayerStatus;
import com.auction.auctionplayer.mapper.AuctionPlayerMapper;
import com.auction.auctionplayer.repository.AuctionPlayerRepository;
import com.auction.exception.BusinessException;
import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.player.repository.PlayerRepository;
import com.auction.tournamentplayer.enums.TournamentPlayerStatus;
import com.auction.tournamentplayer.repository.TournamentPlayerRepository;
import com.auction.tournamentteam.entity.TournamentTeam;
import com.auction.tournamentteam.enums.TournamentTeamStatus;
import com.auction.tournamentteam.repository.TournamentTeamRepository;

@Service
public class AuctionPlayerServiceImpl implements AuctionPlayerService {

    private static final int MAX_SQUAD_SIZE = 15;

    private static final BigDecimal MINIMUM_RESERVE_PER_SLOT =
            new BigDecimal("1000.00");

    private final AuctionPlayerRepository auctionPlayerRepository;
    private final AuctionRepository auctionRepository;
    private final PlayerRepository playerRepository;
    private final TournamentPlayerRepository tournamentPlayerRepository;
    private final AuctionPlayerMapper auctionPlayerMapper;
    private final TournamentTeamRepository tournamentTeamRepository;
    private final AuctionBidRepository auctionBidRepository;

    public AuctionPlayerServiceImpl(
            AuctionPlayerRepository auctionPlayerRepository,
            AuctionRepository auctionRepository,
            PlayerRepository playerRepository,
            TournamentPlayerRepository tournamentPlayerRepository,
            AuctionPlayerMapper auctionPlayerMapper,
            TournamentTeamRepository tournamentTeamRepository,
            AuctionBidRepository auctionBidRepository) {

        this.auctionPlayerRepository = auctionPlayerRepository;
        this.auctionRepository = auctionRepository;
        this.playerRepository = playerRepository;
        this.tournamentPlayerRepository = tournamentPlayerRepository;
        this.auctionPlayerMapper = auctionPlayerMapper;
        this.tournamentTeamRepository = tournamentTeamRepository;
        this.auctionBidRepository = auctionBidRepository;
    }

    @Override
    @Transactional
    public AuctionPlayerResponse create(
            AuctionPlayerCreateRequest request) {

        // 1. Validate auction exists
        UUID auctionId = request.getAuctionId();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Auction not found: " + auctionId));

        // 2. Players can only be added while the auction is in DRAFT status
        if (auction.getStatus() != AuctionStatus.DRAFT) {
            throw new BusinessException(
                    "Players can only be added while the auction is in draft status.");
        }

        // 3. Validate player exists
        UUID playerId = request.getPlayerId();

        var player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Player not found: " + playerId));

        // 4. Prevent duplicate player registration in the same auction
        if (auctionPlayerRepository
                .existsByAuctionIdAndPlayerId(auctionId, playerId)) {

            throw new DuplicateResourceException(
                    "Player is already registered in this auction.");
        }

        // 5. Verify player is selected for the auction's tournament
        UUID tournamentId = auction.getTournament().getId();

        boolean selectedForTournament =
                tournamentPlayerRepository
                        .existsByTournamentIdAndPlayerIdAndStatus(
                                tournamentId,
                                playerId,
                                TournamentPlayerStatus.SELECTED);

        if (!selectedForTournament) {
            throw new BusinessException(
                    "Player must be selected for the auction's tournament "
                    + "before being added to the auction.");
        }

        // 6. Create the auction-player record
        AuctionPlayer auctionPlayer = new AuctionPlayer();
        auctionPlayer.setAuction(auction);
        auctionPlayer.setPlayer(player);
        auctionPlayer.setBasePrice(request.getBasePrice());
        auctionPlayer.setPriority(request.getPriority());
        auctionPlayer.setAuctionStatus(AuctionPlayerStatus.PENDING);
        auctionPlayer.setCurrentBid(null);
        auctionPlayer.setSoldPrice(null);
        auctionPlayer.setSoldToTournamentTeam(null);

        // 7. Save and return the response
        AuctionPlayer saved =
                auctionPlayerRepository.save(auctionPlayer);

        return auctionPlayerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionPlayerResponse> getPlayersByAuction(
            UUID auctionId) {

        if (!auctionRepository.existsById(auctionId)) {
            throw new ResourceNotFoundException(
                    "Auction not found with id: " + auctionId);
        }

        return auctionPlayerRepository
                .findByAuctionIdOrderByPriorityAscCreatedAtAsc(auctionId)
                .stream()
                .map(auctionPlayerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AuctionPlayerResponse startBidding(UUID auctionPlayerId) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository
                .findById(auctionPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Auction player not found: " + auctionPlayerId));

        if (auctionPlayer.getAuctionStatus()
                != AuctionPlayerStatus.PENDING) {
            throw new BusinessException(
                    "Only pending players can be moved into auction.");
        }

        Auction auction = auctionPlayer.getAuction();

        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new BusinessException(
                    "Player bidding can only start while the auction "
                    + "is in progress.");
        }

        auctionPlayer.setAuctionStatus(AuctionPlayerStatus.IN_AUCTION);

        AuctionPlayer saved =
                auctionPlayerRepository.save(auctionPlayer);

        return auctionPlayerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AuctionPlayerResponse markUnsold(UUID auctionPlayerId) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository
                .findById(auctionPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Auction player not found: " + auctionPlayerId));

        if (auctionPlayer.getAuctionStatus()
                != AuctionPlayerStatus.IN_AUCTION) {
            throw new BusinessException(
                    "Only players currently in auction can be marked unsold.");
        }

        Auction auction = auctionPlayer.getAuction();

        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new BusinessException(
                    "Players can only be marked unsold while the auction "
                    + "is in progress.");
        }

        if (auctionBidRepository
                .findFirstByAuctionPlayerIdOrderByBidAmountDescBidTimeAsc(
                        auctionPlayerId)
                .isPresent()) {

            throw new BusinessException(
                    "This player has accepted bids and cannot be marked unsold. "
                    + "Finalize the sale to the highest bidder.");
        }

        auctionPlayer.setAuctionStatus(AuctionPlayerStatus.UNSOLD);

        AuctionPlayer saved =
                auctionPlayerRepository.save(auctionPlayer);

        return auctionPlayerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AuctionPlayerResponse sellPlayer(UUID auctionPlayerId) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository
                .findById(auctionPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Auction player not found: " + auctionPlayerId));

        if (auctionPlayer.getAuctionStatus()
                != AuctionPlayerStatus.IN_AUCTION) {
            throw new BusinessException(
                    "Only players currently in auction can be sold.");
        }

        Auction auction = auctionPlayer.getAuction();

        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            throw new BusinessException(
                    "Players can only be sold while the auction is in progress.");
        }

        // The winning team and sale price must come from the highest accepted bid.
        AuctionBid highestBid = auctionBidRepository
                .findFirstByAuctionPlayerIdOrderByBidAmountDescBidTimeAsc(
                        auctionPlayerId)
                .orElseThrow(() -> new BusinessException(
                        "This player has no accepted bids and cannot be sold. "
                        + "Mark the player unsold instead."));

        TournamentTeam winningTeam = highestBid.getTournamentTeam();
        BigDecimal soldPrice = highestBid.getBidAmount();

        if (winningTeam.getStatus() != TournamentTeamStatus.ACTIVE) {
            throw new BusinessException(
                    "The winning tournament team is not active.");
        }

        UUID auctionTournamentId = auction.getTournament().getId();
        UUID teamTournamentId = winningTeam.getTournament().getId();

        if (!auctionTournamentId.equals(teamTournamentId)) {
            throw new BusinessException(
                    "Winning team does not belong to the auction's tournament.");
        }

        BigDecimal remainingBudget = winningTeam.getRemainingBudget();

        if (remainingBudget == null
                || remainingBudget.compareTo(soldPrice) < 0) {
            throw new BusinessException(
                    "Winning team has insufficient remaining budget.");
        }

        long purchasedPlayers =
                auctionPlayerRepository.countSoldPlayersByTeamAndTournament(
                        winningTeam.getId(),
                        auctionTournamentId,
                        AuctionPlayerStatus.SOLD);

        if (purchasedPlayers >= MAX_SQUAD_SIZE) {
            throw new BusinessException(
                    "Winning team has already reached its 15-player squad limit.");
        }

        long slotsAfterPurchase =
                MAX_SQUAD_SIZE - purchasedPlayers - 1L;

        BigDecimal requiredReserve = MINIMUM_RESERVE_PER_SLOT.multiply(
                BigDecimal.valueOf(slotsAfterPurchase));

        BigDecimal budgetAfterPurchase =
                remainingBudget.subtract(soldPrice);

        if (budgetAfterPurchase.compareTo(requiredReserve) < 0) {
            throw new BusinessException(
                    "The sale would leave insufficient budget to reserve "
                    + "₹1,000 for every remaining squad slot.");
        }

        // Finalize the sale
        auctionPlayer.setSoldPrice(soldPrice);
        auctionPlayer.setSoldToTournamentTeam(winningTeam);
        auctionPlayer.setCurrentBid(soldPrice);
        auctionPlayer.setAuctionStatus(AuctionPlayerStatus.SOLD);

        // Deduct the winning bid from the team's remaining budget
        winningTeam.setRemainingBudget(budgetAfterPurchase);

        auctionPlayerRepository.save(auctionPlayer);
        tournamentTeamRepository.save(winningTeam);

        return auctionPlayerMapper.toResponse(auctionPlayer);
    }
}
