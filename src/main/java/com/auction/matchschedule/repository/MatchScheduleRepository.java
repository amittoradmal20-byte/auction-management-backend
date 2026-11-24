
package com.auction.matchschedule.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.matchschedule.entity.MatchSchedule;
import com.auction.matchschedule.enums.MatchScheduleStatus;

@Repository
public interface MatchScheduleRepository
        extends JpaRepository<MatchSchedule, UUID> {

    // Find all schedules belonging to a tournament
    List<MatchSchedule> findByTournamentIdOrderByCreatedAtDesc(
            UUID tournamentId
    );

    // Find schedules by tournament and status
    List<MatchSchedule> findByTournamentIdAndStatus(
            UUID tournamentId,
            MatchScheduleStatus status
    );

    // Find a specific schedule belonging to a tournament
    Optional<MatchSchedule> findByIdAndTournamentId(
            UUID scheduleId,
            UUID tournamentId
    );

    // Check whether a tournament has a published schedule
    boolean existsByTournamentIdAndStatus(
            UUID tournamentId,
            MatchScheduleStatus status
    );

}
