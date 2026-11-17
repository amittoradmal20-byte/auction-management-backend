
package com.auction.dashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auction.enums.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.cricketgroup.repository.CricketGroupRepository;
import com.auction.dashboard.dto.DashboardSummaryResponse;
import com.auction.player.repository.PlayerRepository;
import com.auction.team.repository.TeamRepository;
import com.auction.tournament.repository.TournamentRepository;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final TournamentRepository tournamentRepository;

    private final CricketGroupRepository cricketGroupRepository;

    private final PlayerRepository playerRepository;

    private final TeamRepository teamRepository;

    private final AuctionRepository auctionRepository;

    public DashboardService(
            TournamentRepository tournamentRepository,
            CricketGroupRepository cricketGroupRepository,
            PlayerRepository playerRepository,
            TeamRepository teamRepository,
            AuctionRepository auctionRepository) {

        this.tournamentRepository = tournamentRepository;
        this.cricketGroupRepository = cricketGroupRepository;
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.auctionRepository = auctionRepository;
    }

    public DashboardSummaryResponse getDashboardSummary() {

        return new DashboardSummaryResponse(
                tournamentRepository.count(),
                cricketGroupRepository.count(),
                playerRepository.count(),
                teamRepository.count(),
                auctionRepository.count(),
                auctionRepository.countByStatus(AuctionStatus.IN_PROGRESS),
                auctionRepository.countByStatus(AuctionStatus.PAUSED)
        );
    }
}
