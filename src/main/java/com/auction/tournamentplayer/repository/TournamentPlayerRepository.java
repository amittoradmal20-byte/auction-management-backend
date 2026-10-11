package com.auction.tournamentplayer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.tournamentplayer.entity.TournamentPlayer;
import com.auction.tournamentplayer.enums.TournamentPlayerStatus;

@Repository
public interface TournamentPlayerRepository
        extends JpaRepository<TournamentPlayer, UUID> {

    boolean existsByTournamentIdAndPlayerId(
            UUID tournamentId,
            UUID playerId);

    boolean existsByTournamentIdAndPlayerIdAndStatus(
            UUID tournamentId,
            UUID playerId,
            TournamentPlayerStatus status);

    List<TournamentPlayer> findByTournamentId(UUID tournamentId);

    List<TournamentPlayer> findByTournamentIdAndStatus(
            UUID tournamentId,
            TournamentPlayerStatus status);
}
