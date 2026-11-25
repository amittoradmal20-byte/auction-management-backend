
package com.auction.matchschedule.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.matchschedule.entity.MatchScheduleFixture;

@Repository
public interface MatchScheduleFixtureRepository
        extends JpaRepository<MatchScheduleFixture, UUID> {

    // Retrieve all fixtures in match-number order
    List<MatchScheduleFixture> findByMatchScheduleIdOrderByMatchNumberAsc(
            UUID matchScheduleId
    );

    // Retrieve fixtures scheduled for a particular date
    List<MatchScheduleFixture>
            findByMatchScheduleIdAndMatchDateOrderByStartTimeAsc(
                    UUID matchScheduleId,
                    LocalDate matchDate
            );

    // Retrieve fixtures belonging to a specific round
    List<MatchScheduleFixture>
            findByMatchScheduleIdAndRoundNumberOrderByMatchNumberAsc(
                    UUID matchScheduleId,
                    Integer roundNumber
            );

    // Retrieve a particular match within a schedule
    Optional<MatchScheduleFixture>
            findByMatchScheduleIdAndMatchNumber(
                    UUID matchScheduleId,
                    Integer matchNumber
            );

    // Retrieve all fixtures involving a particular team
    List<MatchScheduleFixture>
            findByMatchScheduleIdAndHomeTournamentTeamIdOrMatchScheduleIdAndAwayTournamentTeamIdOrderByMatchNumberAsc(
                    UUID homeScheduleId,
                    UUID homeTeamId,
                    UUID awayScheduleId,
                    UUID awayTeamId
            );

    // Count fixtures belonging to a schedule
    long countByMatchScheduleId(
            UUID matchScheduleId
    );

    // Delete fixtures when regenerating a DRAFT schedule
    void deleteByMatchScheduleId(
            UUID matchScheduleId
    );

}
