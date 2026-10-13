package com.auction.auctionplayer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auction.entity.Auction;
import com.auction.auction.repository.AuctionRepository;
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

@Service
public class AuctionPlayerServiceImpl implements AuctionPlayerService {

    private final AuctionPlayerRepository auctionPlayerRepository;
    private final AuctionRepository auctionRepository;
    private final PlayerRepository playerRepository;
    private final TournamentPlayerRepository tournamentPlayerRepository;
    private final AuctionPlayerMapper auctionPlayerMapper;

    public AuctionPlayerServiceImpl(
            AuctionPlayerRepository auctionPlayerRepository,
            AuctionRepository auctionRepository,
            PlayerRepository playerRepository,
            TournamentPlayerRepository tournamentPlayerRepository,
            AuctionPlayerMapper auctionPlayerMapper) {

        this.auctionPlayerRepository = auctionPlayerRepository;
        this.auctionRepository = auctionRepository;
        this.playerRepository = playerRepository;
        this.tournamentPlayerRepository = tournamentPlayerRepository;
        this.auctionPlayerMapper = auctionPlayerMapper;
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

        // 2. Validate player exists
        UUID playerId = request.getPlayerId();

        var player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Player not found: " + playerId));

        // 3. Prevent duplicate player registration in the same auction
        if (auctionPlayerRepository
                .existsByAuctionIdAndPlayerId(auctionId, playerId)) {

            throw new DuplicateResourceException(
                    "Player is already registered in this auction.");
        }

        // 4. Verify player is selected for the auction's tournament
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

        // 5. Create the auction-player record
        AuctionPlayer auctionPlayer = new AuctionPlayer();
        auctionPlayer.setAuction(auction);
        auctionPlayer.setPlayer(player);
        auctionPlayer.setBasePrice(request.getBasePrice());
        auctionPlayer.setPriority(request.getPriority());
        auctionPlayer.setAuctionStatus(AuctionPlayerStatus.PENDING);
        auctionPlayer.setCurrentBid(null);
        auctionPlayer.setSoldPrice(null);
        auctionPlayer.setSoldToTournamentTeam(null);

        // 6. Save and return the response
        AuctionPlayer saved =
                auctionPlayerRepository.save(auctionPlayer);

        return auctionPlayerMapper.toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AuctionPlayerResponse> getPlayersByAuction(UUID auctionId) {

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
}
