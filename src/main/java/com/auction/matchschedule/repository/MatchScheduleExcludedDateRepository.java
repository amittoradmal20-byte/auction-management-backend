
package com.auction.matchschedule.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.matchschedule.entity.MatchScheduleExcludedDate;

@Repository
public interface MatchScheduleExcludedDateRepository
        extends JpaRepository<MatchScheduleExcludedDate, UUID> {

    // Retrieve all excluded dates for a schedule
    List<MatchScheduleExcludedDate>
            findByMatchScheduleIdOrderByExcludedDateAsc(
                    UUID matchScheduleId
            );

    // Check whether a specific date is excluded
    boolean existsByMatchScheduleIdAndExcludedDate(
            UUID matchScheduleId,
            LocalDate excludedDate
    );

    // Count excluded dates for a schedule
    long countByMatchScheduleId(
            UUID matchScheduleId
    );

    // Remove excluded dates when updating a draft schedule
    void deleteByMatchScheduleId(
            UUID matchScheduleId
    );

}
