package com.auction.tournamentplayer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.exception.ResourceNotFoundException;
import com.auction.player.repository.PlayerRepository;
import com.auction.tournament.repository.TournamentRepository;
import com.auction.tournamentplayer.dto.TournamentPlayerCreateRequest;
import com.auction.tournamentplayer.dto.TournamentPlayerResponse;
import com.auction.tournamentplayer.entity.TournamentPlayer;
import com.auction.tournamentplayer.enums.TournamentPlayerStatus;
import com.auction.tournamentplayer.mapper.TournamentPlayerMapper;
import com.auction.tournamentplayer.repository.TournamentPlayerRepository;

@Service
public class TournamentPlayerServiceImpl
        implements TournamentPlayerService {

    private final TournamentPlayerRepository tournamentPlayerRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final TournamentPlayerMapper tournamentPlayerMapper;

    public TournamentPlayerServiceImpl(
            TournamentPlayerRepository tournamentPlayerRepository,
            TournamentRepository tournamentRepository,
            PlayerRepository playerRepository,
            TournamentPlayerMapper tournamentPlayerMapper) {

        this.tournamentPlayerRepository = tournamentPlayerRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.tournamentPlayerMapper = tournamentPlayerMapper;
    }

    @Override
    @Transactional
    public TournamentPlayerResponse registerPlayer(
            TournamentPlayerCreateRequest request) {

        UUID tournamentId = request.getTournamentId();
        UUID playerId = request.getPlayerId();

        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tournament not found: " + tournamentId));

        var player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Player not found: " + playerId));

        if (tournamentPlayerRepository
                .existsByTournamentIdAndPlayerId(tournamentId, playerId)) {
            throw new IllegalArgumentException(
                    "Player is already registered for this tournament.");
        }

        TournamentPlayer tournamentPlayer = new TournamentPlayer();
        tournamentPlayer.setTournament(tournament);
        tournamentPlayer.setPlayer(player);
        tournamentPlayer.setStatus(TournamentPlayerStatus.SELECTED);

        TournamentPlayer saved =
                tournamentPlayerRepository.save(tournamentPlayer);

        return tournamentPlayerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentPlayerResponse> getPlayersByTournament(
            UUID tournamentId) {

        if (!tournamentRepository.existsById(tournamentId)) {
            throw new ResourceNotFoundException(
                    "Tournament not found: " + tournamentId);
        }

        return tournamentPlayerRepository
                .findByTournamentId(tournamentId)
                .stream()
                .map(tournamentPlayerMapper::toResponse)
                .toList();
    }
}
