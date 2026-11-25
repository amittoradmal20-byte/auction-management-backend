
package com.auction.matchschedule.engine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.engine.CalendarFixtureAllocator.AllocatedFixture;

@Component
public class AllocatedFixtureValidator {

    private final AllowedPlayingDateGenerator allowedPlayingDateGenerator;

    public AllocatedFixtureValidator(
            AllowedPlayingDateGenerator allowedPlayingDateGenerator) {

        this.allowedPlayingDateGenerator = allowedPlayingDateGenerator;
    }

    public void validate(
            List<AllocatedFixture> fixtures,
            SchedulingCalendarConfig config) {

        if (fixtures == null || fixtures.isEmpty()) {
            throw new IllegalArgumentException(
                    "Allocated fixtures cannot be empty."
            );
        }

        if (config == null) {
            throw new IllegalArgumentException(
                    "Scheduling configuration is required."
            );
        }

        validateLimits(config);

        Set<LocalDate> allowedDates = new HashSet<>(
                allowedPlayingDateGenerator.generate(config)
        );

        List<AllocatedFixture> chronological =
                new ArrayList<>(fixtures);

        chronological.sort(
                Comparator.comparing(
                        (AllocatedFixture f) ->
                                LocalDateTime.of(
                                        f.matchDate(),
                                        f.startTime()
                                )
                ).thenComparingInt(
                        AllocatedFixture::groundNumber
                ).thenComparingInt(
                        AllocatedFixture::matchNumber
                )
        );

        Map<UUID, LocalDateTime> lastTeamEnd = new HashMap<>();

        Map<UUID, Map<LocalDate, Integer>> dailyCounts =
                new HashMap<>();

        Map<Integer, LocalDateTime> lastGroundEnd =
                new HashMap<>();

        Map<UUID, Integer> lastAppearanceIndex =
                new HashMap<>();

        Set<Integer> matchNumbers = new HashSet<>();

        for (int i = 0; i < chronological.size(); i++) {

            AllocatedFixture fixture = chronological.get(i);

            validateFixtureFields(fixture, config, allowedDates);

            if (!matchNumbers.add(fixture.matchNumber())) {
                throw new IllegalStateException(
                        "Duplicate match number: "
                        + fixture.matchNumber()
                );
            }

            UUID homeId = fixture.homeTournamentTeamId();
            UUID awayId = fixture.awayTournamentTeamId();

            LocalDateTime start = LocalDateTime.of(
                    fixture.matchDate(),
                    fixture.startTime()
            );

            LocalDateTime end = LocalDateTime.of(
                    fixture.matchDate(),
                    fixture.endTime()
            );

            // Ground availability
            LocalDateTime previousGroundEnd =
                    lastGroundEnd.get(fixture.groundNumber());

            if (previousGroundEnd != null) {

                LocalDateTime earliestGroundStart =
                        previousGroundEnd.plusMinutes(
                                config.getBreakBetweenMatchesMinutes()
                        );

                if (start.isBefore(earliestGroundStart)) {
                    throw new IllegalStateException(
                            "Ground " + fixture.groundNumber()
                            + " has overlapping matches or "
                            + "insufficient break before match "
                            + fixture.matchNumber()
                    );
                }
            }

            lastGroundEnd.put(fixture.groundNumber(), end);

            // Team availability and minimum rest
            validateTeamAvailability(
                    homeId, start, fixture.matchNumber(),
                    lastTeamEnd, config
            );

            validateTeamAvailability(
                    awayId, start, fixture.matchNumber(),
                    lastTeamEnd, config
            );

            // Minimum intervening matches
            validateAppearanceGap(
                    homeId, i, fixture.matchNumber(),
                    lastAppearanceIndex, config
            );

            validateAppearanceGap(
                    awayId, i, fixture.matchNumber(),
                    lastAppearanceIndex, config
            );

            // Daily team match limits
            incrementAndValidateDailyCount(
                    homeId, fixture.matchDate(),
                    dailyCounts, config
            );

            incrementAndValidateDailyCount(
                    awayId, fixture.matchDate(),
                    dailyCounts, config
            );

            lastTeamEnd.put(homeId, end);
            lastTeamEnd.put(awayId, end);

            lastAppearanceIndex.put(homeId, i);
            lastAppearanceIndex.put(awayId, i);
        }
    }

    private void validateLimits(SchedulingCalendarConfig config) {

        if (config.getMinimumRestMinutes() == null
                || config.getMinimumRestMinutes() < 0) {
            throw new IllegalArgumentException(
                    "Minimum rest minutes must be zero or greater."
            );
        }

        if (config.getBreakBetweenMatchesMinutes() == null
                || config.getBreakBetweenMatchesMinutes() < 0) {
            throw new IllegalArgumentException(
                    "Break between matches must be zero or greater."
            );
        }

        if (config.getMinimumMatchesBetweenAppearances() == null
                || config.getMinimumMatchesBetweenAppearances() < 0) {
            throw new IllegalArgumentException(
                    "Minimum matches between appearances "
                    + "must be zero or greater."
            );
        }

        if (config.getMaxMatchesPerTeamPerDay() == null
                || config.getMaxMatchesPerTeamPerDay() <= 0) {
            throw new IllegalArgumentException(
                    "Maximum matches per team per day "
                    + "must be greater than zero."
            );
        }

        if (config.getNumberOfGrounds() == null
                || config.getNumberOfGrounds() <= 0) {
            throw new IllegalArgumentException(
                    "Number of grounds must be greater than zero."
            );
        }
    }

    private void validateFixtureFields(
            AllocatedFixture fixture,
            SchedulingCalendarConfig config,
            Set<LocalDate> allowedDates) {

        if (fixture == null
                || fixture.matchDate() == null
                || fixture.startTime() == null
                || fixture.endTime() == null
                || fixture.homeTournamentTeamId() == null
                || fixture.awayTournamentTeamId() == null) {
            throw new IllegalArgumentException(
                    "Fixture contains missing required fields."
            );
        }

        if (fixture.matchNumber() <= 0
                || fixture.roundNumber() <= 0) {
            throw new IllegalStateException(
                    "Match and round numbers must be positive."
            );
        }

        if (fixture.homeTournamentTeamId().equals(
                fixture.awayTournamentTeamId())) {
            throw new IllegalStateException(
                    "A team cannot play against itself."
            );
        }

        if (!allowedDates.contains(fixture.matchDate())) {
            throw new IllegalStateException(
                    "Match " + fixture.matchNumber()
                    + " is scheduled on a disallowed date: "
                    + fixture.matchDate()
            );
        }

        if (fixture.groundNumber() < 1
                || fixture.groundNumber() > config.getNumberOfGrounds()) {
            throw new IllegalStateException(
                    "Invalid ground number for match "
                    + fixture.matchNumber()
            );
        }

        if (!fixture.endTime().isAfter(fixture.startTime())) {
            throw new IllegalStateException(
                    "Invalid match time for match "
                    + fixture.matchNumber()
            );
        }

        if (config.getDailyStartTime() == null
                || config.getDailyEndTime() == null
                || fixture.startTime().isBefore(
                        config.getDailyStartTime())
                || fixture.endTime().isAfter(
                        config.getDailyEndTime())) {
            throw new IllegalStateException(
                    "Match " + fixture.matchNumber()
                    + " is outside permitted playing hours."
            );
        }

        if (config.getMatchDurationMinutes() == null
                || java.time.Duration.between(
                        fixture.startTime(),
                        fixture.endTime()
                ).toMinutes() != config.getMatchDurationMinutes()) {
            throw new IllegalStateException(
                    "Incorrect match duration for match "
                    + fixture.matchNumber()
            );
        }
    }

    private void validateTeamAvailability(
            UUID teamId,
            LocalDateTime start,
            int matchNumber,
            Map<UUID, LocalDateTime> lastTeamEnd,
            SchedulingCalendarConfig config) {

        LocalDateTime previousEnd = lastTeamEnd.get(teamId);

        if (previousEnd == null) {
            return;
        }

        LocalDateTime earliestStart =
                previousEnd.plusMinutes(
                        config.getMinimumRestMinutes()
                );

        if (start.isBefore(earliestStart)) {
            throw new IllegalStateException(
                    "Team " + teamId
                    + " has overlapping matches or insufficient "
                    + "rest before match " + matchNumber
            );
        }
    }

    private void validateAppearanceGap(
            UUID teamId,
            int currentIndex,
            int matchNumber,
            Map<UUID, Integer> lastAppearanceIndex,
            SchedulingCalendarConfig config) {

        Integer previousIndex = lastAppearanceIndex.get(teamId);

        if (previousIndex == null) {
            return;
        }

        int matchesBetween =
                currentIndex - previousIndex - 1;

        if (matchesBetween
                < config.getMinimumMatchesBetweenAppearances()) {
            throw new IllegalStateException(
                    "Team " + teamId
                    + " violates minimum intervening match rule "
                    + "at match " + matchNumber
            );
        }
    }

    private void incrementAndValidateDailyCount(
            UUID teamId,
            LocalDate date,
            Map<UUID, Map<LocalDate, Integer>> dailyCounts,
            SchedulingCalendarConfig config) {

        int count = dailyCounts
                .computeIfAbsent(teamId, key -> new HashMap<>())
                .merge(date, 1, Integer::sum);

        if (count > config.getMaxMatchesPerTeamPerDay()) {
            throw new IllegalStateException(
                    "Team " + teamId
                    + " exceeds the daily match limit on " + date
            );
        }
    }

}
