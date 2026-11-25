package com.auction.matchschedule.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.engine.RoundRobinGenerator.RoundRobinMatch;

@Component
public class FairMatchOrderingEngine {

    // ==========================================
    // CONFIGURATION
    // ==========================================

    private static final int MAX_ORDERING_ATTEMPTS = 100;

    // ==========================================
    // 1. ARRANGE LEAGUE FIXTURES
    // ==========================================

    /**
     * Reorders league fixtures to prevent consecutive
     * appearances by the same team.
     *
     * Supports:
     * - Single Round Robin
     * - Double Round Robin
     * - Partial Round Robin
     *
     * Preserves all original fixtures without duplication.
     *
     * Retries with deterministic random seeds when an
     * ordering attempt reaches a dead end.
     */
    public List<RoundRobinMatch> arrange(
            List<RoundRobinMatch> fixtures,
            long randomSeed) {

        if (fixtures == null) {

            throw new IllegalArgumentException(
                    "Fixtures cannot be null."
            );
        }

        if (fixtures.isEmpty()) {
            return List.of();
        }

        // Validate input before attempting ordering.

        validateInputFixtures(fixtures);

        // Generate deterministic seeds for retry attempts.

        Random seedGenerator = new Random(randomSeed);

        for (int attempt = 0;
                attempt < MAX_ORDERING_ATTEMPTS;
                attempt++) {

            // Preserve original seed for the first attempt.

            long attemptSeed =
                    attempt == 0
                            ? randomSeed
                            : seedGenerator.nextLong();

            List<RoundRobinMatch> ordered =
                    tryArrange(
                            fixtures,
                            attemptSeed
                    );

            // Null indicates that this attempt
            // reached a dead end.

            if (ordered != null) {

                validateNoConsecutiveMatches(ordered);

                return List.copyOf(ordered);
            }
        }

        throw new IllegalStateException(
                "Unable to generate a schedule without "
                + "consecutive matches for the same team "
                + "after " + MAX_ORDERING_ATTEMPTS
                + " ordering attempts. "
                + "Try changing the tournament configuration "
                + "or random seed."
        );
    }

    // ==========================================
    // 2. ATTEMPT FIXTURE ORDERING
    // ==========================================

    private List<RoundRobinMatch> tryArrange(
            List<RoundRobinMatch> fixtures,
            long randomSeed) {

        // Each attempt starts with the original fixtures.

        List<RoundRobinMatch> remaining =
                new ArrayList<>(fixtures);

        List<RoundRobinMatch> ordered =
                new ArrayList<>();

        Map<UUID, Integer> appearanceCount =
                new HashMap<>();

        Random random = new Random(randomSeed);

        while (!remaining.isEmpty()) {

            RoundRobinMatch previous =
                    ordered.isEmpty()
                            ? null
                            : ordered.get(ordered.size() - 1);

            List<RoundRobinMatch> eligible =
                    new ArrayList<>();

            // ==========================================
            // IDENTIFY ELIGIBLE FIXTURES
            // ==========================================

            for (RoundRobinMatch candidate : remaining) {

                if (previous == null
                        || !sharesTeam(previous, candidate)) {

                    eligible.add(candidate);
                }
            }

            // ==========================================
            // HANDLE ORDERING DEAD END
            // ==========================================

            if (eligible.isEmpty()) {

                // Return null instead of throwing immediately.
                // The arrange() method will retry using
                // another deterministic random seed.

                return null;
            }

            // ==========================================
            // PRIORITIZE TEAMS WITH FEWER APPEARANCES
            // ==========================================

            eligible.sort(
                    Comparator.comparingInt(
                            match -> calculateAppearanceScore(
                                    match,
                                    appearanceCount
                            )
                    )
            );

            int lowestScore =
                    calculateAppearanceScore(
                            eligible.get(0),
                            appearanceCount
                    );

            List<RoundRobinMatch> bestCandidates =
                    new ArrayList<>();

            for (RoundRobinMatch candidate : eligible) {

                int score =
                        calculateAppearanceScore(
                                candidate,
                                appearanceCount
                        );

                if (score == lowestScore) {

                    bestCandidates.add(candidate);

                } else {

                    break;
                }
            }

            // ==========================================
            // RANDOMLY SELECT BEST ELIGIBLE FIXTURE
            // ==========================================

            RoundRobinMatch selected =
                    bestCandidates.get(
                            random.nextInt(
                                    bestCandidates.size()
                            )
                    );

            ordered.add(selected);

            remaining.remove(selected);

            // ==========================================
            // UPDATE TEAM APPEARANCE COUNTS
            // ==========================================

            appearanceCount.merge(
                    selected.homeTeam().getId(),
                    1,
                    Integer::sum
            );

            appearanceCount.merge(
                    selected.awayTeam().getId(),
                    1,
                    Integer::sum
            );
        }

        return ordered;
    }

    // ==========================================
    // 3. CALCULATE TEAM APPEARANCE SCORE
    // ==========================================

    private int calculateAppearanceScore(
            RoundRobinMatch match,
            Map<UUID, Integer> appearanceCount) {

        return appearanceCount.getOrDefault(
                match.homeTeam().getId(),
                0
        ) + appearanceCount.getOrDefault(
                match.awayTeam().getId(),
                0
        );
    }

    // ==========================================
    // 4. CHECK WHETHER FIXTURES SHARE A TEAM
    // ==========================================

    private boolean sharesTeam(
            RoundRobinMatch first,
            RoundRobinMatch second) {

        UUID firstHomeTeamId =
                first.homeTeam().getId();

        UUID firstAwayTeamId =
                first.awayTeam().getId();

        UUID secondHomeTeamId =
                second.homeTeam().getId();

        UUID secondAwayTeamId =
                second.awayTeam().getId();

        return firstHomeTeamId.equals(secondHomeTeamId)
                || firstHomeTeamId.equals(secondAwayTeamId)
                || firstAwayTeamId.equals(secondHomeTeamId)
                || firstAwayTeamId.equals(secondAwayTeamId);
    }

    // ==========================================
    // 5. VALIDATE INPUT FIXTURES
    // ==========================================

    private void validateInputFixtures(
            List<RoundRobinMatch> fixtures) {

        for (RoundRobinMatch fixture : fixtures) {

            if (fixture == null
                    || fixture.homeTeam() == null
                    || fixture.awayTeam() == null) {

                throw new IllegalArgumentException(
                        "Fixture contains invalid teams."
                );
            }

            UUID homeTeamId =
                    fixture.homeTeam().getId();

            UUID awayTeamId =
                    fixture.awayTeam().getId();

            if (homeTeamId == null
                    || awayTeamId == null) {

                throw new IllegalArgumentException(
                        "Fixture contains invalid team IDs."
                );
            }

            if (homeTeamId.equals(awayTeamId)) {

                throw new IllegalArgumentException(
                        "A team cannot play against itself."
                );
            }
        }
    }

    // ==========================================
    // 6. VALIDATE NO CONSECUTIVE MATCHES
    // ==========================================

    private void validateNoConsecutiveMatches(
            List<RoundRobinMatch> fixtures) {

        for (int i = 1; i < fixtures.size(); i++) {

            if (sharesTeam(
                    fixtures.get(i - 1),
                    fixtures.get(i))) {

                throw new IllegalStateException(
                        "Consecutive match validation failed "
                        + "at match number " + (i + 1)
                );
            }
        }
    }
}