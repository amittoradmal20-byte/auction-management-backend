
package com.auction.matchschedule.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.matchschedule.entity.MatchScheduleTeamDraw;

@Repository
public interface MatchScheduleTeamDrawRepository
        extends JpaRepository<MatchScheduleTeamDraw, UUID> {

    // Retrieve all teams in their randomized draw order
    List<MatchScheduleTeamDraw> findByMatchScheduleIdOrderByRandomNumberAsc(
            UUID matchScheduleId
    );

    // Check whether a team is already included in a schedule
    boolean existsByMatchScheduleIdAndTournamentTeamId(
            UUID matchScheduleId,
            UUID tournamentTeamId
    );

    // Count participating teams in a schedule
    long countByMatchScheduleId(
            UUID matchScheduleId
    );

    // Remove randomized assignments for a draft schedule
    void deleteByMatchScheduleId(
            UUID matchScheduleId
    );

}
