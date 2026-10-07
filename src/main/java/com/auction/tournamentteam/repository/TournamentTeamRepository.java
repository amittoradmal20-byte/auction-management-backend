package com.auction.tournamentteam.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.tournamentteam.entity.TournamentTeam;

@Repository
public interface TournamentTeamRepository
        extends JpaRepository<TournamentTeam, UUID> {

    boolean existsByTournamentIdAndTeamId(
            UUID tournamentId,
            UUID teamId);

    boolean existsByTournamentIdAndOwnerId(
            UUID tournamentId,
            UUID ownerId);

    List<TournamentTeam> findByTournamentId(
            UUID tournamentId);

    List<TournamentTeam> findByTournamentIdOrderByTeam_NameAsc(
            UUID tournamentId);
}