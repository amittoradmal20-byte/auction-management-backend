
package com.auction.matchschedule.engine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.engine.MatchTimeSlotGenerator.MatchTimeSlot;
import com.auction.matchschedule.engine.RoundRobinGenerator.RoundRobinMatch;

@Component
public class CalendarFixtureAllocator {

    /**
     * Assigns fixtures to available calendar slots.
     *
     * The incoming fixture order is preserved.
     */
    public List<AllocatedFixture> allocate(
            List<RoundRobinMatch> orderedMatches,
            List<MatchTimeSlot> availableSlots,
            SchedulingCalendarConfig config) {

        validateInputs(orderedMatches, availableSlots, config);

        List<AllocatedFixture> allocated = new ArrayList<>();

        Map<UUID, LocalDateTime> lastTeamEndTime =
                new HashMap<>();

        Map<UUID, Map<LocalDate, Integer>> dailyTeamMatches =
                new HashMap<>();

        Set<MatchTimeSlot> usedSlots = new HashSet<>();

        int nextSlotIndex = 0;

        for (RoundRobinMatch match : orderedMatches) {

            UUID homeId = match.homeTeam().getId();
            UUID awayId = match.awayTeam().getId();

            MatchTimeSlot selectedSlot = null;
            int selectedIndex = -1;

            for (int i = nextSlotIndex;
                    i < availableSlots.size();
                    i++) {

                MatchTimeSlot candidate = availableSlots.get(i);

                if (usedSlots.contains(candidate)) {
                    continue;
                }

                if (!isTeamAvailable(
                        homeId,
                        candidate,
                        lastTeamEndTime,
                        dailyTeamMatches,
                        config)) {
                    continue;
                }

                if (!isTeamAvailable(
                        awayId,
                        candidate,
                        lastTeamEndTime,
                        dailyTeamMatches,
                        config)) {
                    continue;
                }

                selectedSlot = candidate;
                selectedIndex = i;
                break;
            }

            if (selectedSlot == null) {
                throw new IllegalStateException(
                        "Unable to allocate match "
                        + (allocated.size() + 1)
                        + ". Not enough valid calendar slots "
                        + "for the configured rest periods, "
                        + "daily match limits, or playing dates."
                );
            }

            allocated.add(
                    new AllocatedFixture(
                            allocated.size() + 1,
                            match.roundNumber(),
                            match.homeTeam().getId(),
                            match.awayTeam().getId(),
                            selectedSlot.matchDate(),
                            selectedSlot.startTime(),
                            selectedSlot.endTime(),
                            selectedSlot.groundNumber()
                    )
            );

            usedSlots.add(selectedSlot);

            LocalDateTime matchEnd =
                    selectedSlot.endDateTime();

            lastTeamEndTime.put(homeId, matchEnd);
            lastTeamEndTime.put(awayId, matchEnd);

            incrementDailyMatchCount(
                    homeId,
                    selectedSlot.matchDate(),
                    dailyTeamMatches
            );

            incrementDailyMatchCount(
                    awayId,
                    selectedSlot.matchDate(),
                    dailyTeamMatches
            );

            nextSlotIndex = selectedIndex + 1;
        }

        return List.copyOf(allocated);
    }

    private boolean isTeamAvailable(
            UUID teamId,
            MatchTimeSlot slot,
            Map<UUID, LocalDateTime> lastTeamEndTime,
            Map<UUID, Map<LocalDate, Integer>> dailyTeamMatches,
            SchedulingCalendarConfig config) {

        LocalDateTime previousEnd =
                lastTeamEndTime.get(teamId);

        if (previousEnd != null) {

            LocalDateTime earliestNextStart =
                    previousEnd.plusMinutes(
                            config.getMinimumRestMinutes()
                    );

            if (slot.startDateTime().isBefore(
                    earliestNextStart)) {
                return false;
            }
        }

        int matchesToday =
                dailyTeamMatches
                        .getOrDefault(teamId, Map.of())
                        .getOrDefault(slot.matchDate(), 0);

        return matchesToday
                < config.getMaxMatchesPerTeamPerDay();
    }

    private void incrementDailyMatchCount(
            UUID teamId,
            LocalDate matchDate,
            Map<UUID, Map<LocalDate, Integer>> dailyTeamMatches) {

        dailyTeamMatches
                .computeIfAbsent(
                        teamId,
                        key -> new HashMap<>()
                )
                .merge(matchDate, 1, Integer::sum);
    }

    private void validateInputs(
            List<RoundRobinMatch> matches,
            List<MatchTimeSlot> slots,
            SchedulingCalendarConfig config) {

        if (matches == null || matches.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one match is required."
            );
        }

        if (slots == null || slots.isEmpty()) {
            throw new IllegalArgumentException(
                    "No available match time slots."
            );
        }

        if (config == null) {
            throw new IllegalArgumentException(
                    "Scheduling configuration is required."
            );
        }

        if (config.getMinimumRestMinutes() == null
                || config.getMinimumRestMinutes() < 0) {
            throw new IllegalArgumentException(
                    "Minimum rest minutes cannot be negative."
            );
        }

        if (config.getMaxMatchesPerTeamPerDay() == null
                || config.getMaxMatchesPerTeamPerDay() <= 0) {
            throw new IllegalArgumentException(
                    "Maximum matches per team per day must be positive."
            );
        }

        if (config.getMinimumMatchesBetweenAppearances() == null
                || config.getMinimumMatchesBetweenAppearances() < 0) {
            throw new IllegalArgumentException(
                    "Minimum matches between appearances cannot be negative."
            );
        }

        for (int i = 0; i < matches.size(); i++) {

            RoundRobinMatch match = matches.get(i);

            if (match == null
                    || match.homeTeam() == null
                    || match.awayTeam() == null
                    || match.homeTeam().getId() == null
                    || match.awayTeam().getId() == null) {
                throw new IllegalArgumentException(
                        "Invalid fixture at position " + (i + 1)
                );
            }
        }

        for (int i = 1; i < matches.size(); i++) {

            RoundRobinMatch current = matches.get(i);

            for (int gap = 1;
                    gap <= config.getMinimumMatchesBetweenAppearances()
                            && i - gap >= 0;
                    gap++) {

                RoundRobinMatch previous =
                        matches.get(i - gap);

                if (sharesTeam(current, previous)) {
                    throw new IllegalArgumentException(
                            "Fixture order violates minimum "
                            + "matches-between-appearances rule "
                            + "at match " + (i + 1)
                    );
                }
            }
        }
    }

    private boolean sharesTeam(
            RoundRobinMatch first,
            RoundRobinMatch second) {

        UUID home = first.homeTeam().getId();
        UUID away = first.awayTeam().getId();

        return home.equals(second.homeTeam().getId())
                || home.equals(second.awayTeam().getId())
                || away.equals(second.homeTeam().getId())
                || away.equals(second.awayTeam().getId());
    }

    /**
     * A fixture with assigned date, time and ground.
     */
    public record AllocatedFixture(
            int matchNumber,
            int roundNumber,
            UUID homeTournamentTeamId,
            UUID awayTournamentTeamId,
            LocalDate matchDate,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime,
            int groundNumber
    ) {
    }

}
