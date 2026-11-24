
package com.auction.matchschedule.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.engine.RoundRobinGenerator.RoundRobinMatch;

@Component
public class MatchScheduleFairnessValidator {

    /**
     * Validates match ordering and team participation.
     *
     * This method checks:
     * 1. No null fixtures.
     * 2. No team plays against itself.
     * 3. No duplicate fixture pairings.
     * 4. Minimum number of matches between appearances.
     *
     * For double round robin, the same pairing is
     * permitted twice, but not more than twice.
     */
    public void validate(
            List<RoundRobinMatch> fixtures,
            boolean doubleRoundRobin,
            int minimumMatchesBetweenAppearances) {

        if (fixtures == null || fixtures.isEmpty()) {
            throw new IllegalArgumentException(
                    "Match fixtures cannot be empty."
            );
        }

        if (minimumMatchesBetweenAppearances < 0) {
            throw new IllegalArgumentException(
                    "Minimum matches between appearances cannot be negative."
            );
        }

        Map<String, Integer> pairingCount = new HashMap<>();

        Map<UUID, Integer> lastAppearanceIndex = new HashMap<>();

        Set<UUID> participatingTeams = new HashSet<>();

        for (int i = 0; i < fixtures.size(); i++) {

            RoundRobinMatch match = fixtures.get(i);

            if (match == null
                    || match.homeTeam() == null
                    || match.awayTeam() == null
                    || match.homeTeam().getId() == null
                    || match.awayTeam().getId() == null) {

                throw new IllegalStateException(
                        "Invalid team assignment at match "
                        + (i + 1)
                );
            }

            UUID homeId = match.homeTeam().getId();
            UUID awayId = match.awayTeam().getId();

            if (homeId.equals(awayId)) {
                throw new IllegalStateException(
                        "A team cannot play against itself at match "
                        + (i + 1)
                );
            }

            participatingTeams.add(homeId);
            participatingTeams.add(awayId);

            // Pairing key is independent of home/away order.
            String pairingKey =
                    homeId.compareTo(awayId) < 0
                            ? homeId + ":" + awayId
                            : awayId + ":" + homeId;

            int count = pairingCount.merge(
                    pairingKey, 1, Integer::sum
            );

            int maximumPairings =
                    doubleRoundRobin ? 2 : 1;

            if (count > maximumPairings) {
                throw new IllegalStateException(
                        "Duplicate pairing exceeds the allowed limit "
                        + "at match " + (i + 1)
                );
            }

            validateRestGap(
                    homeId,
                    i,
                    lastAppearanceIndex,
                    minimumMatchesBetweenAppearances
            );

            validateRestGap(
                    awayId,
                    i,
                    lastAppearanceIndex,
                    minimumMatchesBetweenAppearances
            );

            lastAppearanceIndex.put(homeId, i);
            lastAppearanceIndex.put(awayId, i);
        }

        validateExpectedMatchCount(
                fixtures.size(),
                participatingTeams.size(),
                doubleRoundRobin
        );

        validatePairingCoverage(
                pairingCount,
                participatingTeams.size(),
                doubleRoundRobin
        );
    }

    private void validateRestGap(
            UUID teamId,
            int currentIndex,
            Map<UUID, Integer> lastAppearanceIndex,
            int minimumGap) {

        Integer previousIndex =
                lastAppearanceIndex.get(teamId);

        if (previousIndex == null) {
            return;
        }

        int matchesBetween =
                currentIndex - previousIndex - 1;

        if (matchesBetween < minimumGap) {
            throw new IllegalStateException(
                    "Team " + teamId
                    + " has only " + matchesBetween
                    + " matches between appearances. "
                    + "Required minimum: " + minimumGap
                    + ". Violation at match "
                    + (currentIndex + 1)
            );
        }
    }

    private void validateExpectedMatchCount(
            int actualMatches,
            int teamCount,
            boolean doubleRoundRobin) {

        long expectedMatches =
                (long) teamCount * (teamCount - 1) / 2;

        if (doubleRoundRobin) {
            expectedMatches *= 2;
        }

        if (actualMatches != expectedMatches) {
            throw new IllegalStateException(
                    "Incorrect match count. Expected "
                    + expectedMatches
                    + " but found " + actualMatches
            );
        }
    }

    private void validatePairingCoverage(
            Map<String, Integer> pairingCount,
            int teamCount,
            boolean doubleRoundRobin) {

        long expectedUniquePairings =
                (long) teamCount * (teamCount - 1) / 2;

        if (pairingCount.size() != expectedUniquePairings) {
            throw new IllegalStateException(
                    "Not all teams have played every opponent."
            );
        }

        int requiredPairings =
                doubleRoundRobin ? 2 : 1;

        for (Integer count : pairingCount.values()) {

            if (count != requiredPairings) {
                throw new IllegalStateException(
                        "Every team pairing must occur exactly "
                        + requiredPairings + " time(s)."
                );
            }
        }
    }

}
