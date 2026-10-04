package com.auction.team.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.team.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    boolean existsByTournamentIdAndNameIgnoreCase(
            UUID tournamentId,
            String name);

    boolean existsByTournamentIdAndShortNameIgnoreCase(
            UUID tournamentId,
            String shortName);

    List<Team> findByTournamentId(UUID tournamentId);

    boolean existsByOwnerIdAndTournamentId(
            UUID ownerId,
            UUID tournamentId);
}