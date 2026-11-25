
package com.auction.tournamentteam.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.tournamentteam.entity.TournamentTeam;
import com.auction.tournamentteam.enums.TournamentTeamStatus;

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

    // Fetch eligible tournament teams with their team details
    // for match schedule generation.
    @EntityGraph(attributePaths = {"team"})
    List<TournamentTeam> findByTournamentIdAndStatus(
            UUID tournamentId,
            TournamentTeamStatus status);
}
