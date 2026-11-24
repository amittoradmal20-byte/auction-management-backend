
package com.auction.matchschedule.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.engine.RoundRobinGenerator.RoundRobinMatch;

@Component
public class FairMatchOrderingEngine {

    /**
     * Reorders round-robin fixtures to avoid consecutive
     * appearances by the same team whenever possible.
     *
     * Preserves all original fixtures without duplication.
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

            for (RoundRobinMatch candidate : remaining) {

                if (previous == null
                        || !sharesTeam(previous, candidate)) {

                    eligible.add(candidate);
                }
            }

            if (eligible.isEmpty()) {
                throw new IllegalStateException(
                        "Unable to generate a schedule without "
                        + "consecutive matches for the same team. "
                        + "Try changing the tournament configuration."
                );
            }

            // Prefer teams with fewer scheduled appearances.
            eligible.sort(
                    Comparator.comparingInt(
                            match ->
                                    appearanceCount.getOrDefault(
                                            match.homeTeam().getId(), 0
                                    )
                                    +
                                    appearanceCount.getOrDefault(
                                            match.awayTeam().getId(), 0
                                    )
                    )
            );

            int lowestScore = appearanceCount.getOrDefault(
                    eligible.get(0).homeTeam().getId(), 0
            ) + appearanceCount.getOrDefault(
                    eligible.get(0).awayTeam().getId(), 0
            );

            List<RoundRobinMatch> bestCandidates =
                    new ArrayList<>();

            for (RoundRobinMatch candidate : eligible) {

                int score =
                        appearanceCount.getOrDefault(
                                candidate.homeTeam().getId(), 0
                        )
                        +
                        appearanceCount.getOrDefault(
                                candidate.awayTeam().getId(), 0
                        );

                if (score == lowestScore) {
                    bestCandidates.add(candidate);
                } else {
                    break;
                }
            }

            RoundRobinMatch selected =
                    bestCandidates.get(
                            random.nextInt(bestCandidates.size())
                    );

            ordered.add(selected);
            remaining.remove(selected);

            appearanceCount.merge(
                    selected.homeTeam().getId(), 1, Integer::sum
            );

            appearanceCount.merge(
                    selected.awayTeam().getId(), 1, Integer::sum
            );
        }

        validateNoConsecutiveMatches(ordered);

        return List.copyOf(ordered);
    }

    private boolean sharesTeam(
            RoundRobinMatch first,
            RoundRobinMatch second) {

        Set<UUID> firstTeams = new HashSet<>();

        firstTeams.add(first.homeTeam().getId());
        firstTeams.add(first.awayTeam().getId());

        return firstTeams.contains(
                second.homeTeam().getId()
        ) || firstTeams.contains(
                second.awayTeam().getId()
        );
    }

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
