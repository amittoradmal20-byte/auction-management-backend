
package com.auction.matchschedule.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auction.matchschedule.engine.RoundRobinGenerator.RoundRobinMatch;
import com.auction.tournamentteam.entity.TournamentTeam;

class FairMatchOrderingEngineTest {

    private FairMatchOrderingEngine orderingEngine;

    private RoundRobinGenerator roundRobinGenerator;

    @BeforeEach
    void setUp() {

        orderingEngine = new FairMatchOrderingEngine();

        roundRobinGenerator = new RoundRobinGenerator();
    }

    // =========================================================
    // 1. INPUT VALIDATION TESTS
    // =========================================================

    @Test
    void shouldRejectNullFixtures() {

        assertThrows(
                IllegalArgumentException.class,
                () -> orderingEngine.arrange(null, 12345L)
        );
    }

    @Test
    void shouldReturnEmptyListForEmptyFixtures() {

        List<RoundRobinMatch> result =
                orderingEngine.arrange(List.of(), 12345L);

        assertTrue(result.isEmpty());
    }

    // =========================================================
    // 2. FIXTURE INTEGRITY TESTS
    // =========================================================

    @Test
    void shouldPreserveAllFixtures() {

        List<TournamentTeam> teams = createTeams(6);

        List<RoundRobinMatch> original =
                roundRobinGenerator.generate(teams, false);

        List<RoundRobinMatch> ordered =
                orderingEngine.arrange(original, 12345L);

        assertEquals(original.size(), ordered.size());

        assertEquals(
                new HashSet<>(original),
                new HashSet<>(ordered)
        );
    }

    // =========================================================
    // 3. FAIRNESS VALIDATION TESTS
    // =========================================================

    @Test
    void shouldPreventConsecutiveTeamAppearances() {

        List<TournamentTeam> teams = createTeams(6);

        List<RoundRobinMatch> fixtures =
                roundRobinGenerator.generate(teams, false);

        List<RoundRobinMatch> ordered =
                orderingEngine.arrange(fixtures, 12345L);

        for (int i = 1; i < ordered.size(); i++) {

            assertFalse(
                    sharesTeam(
                            ordered.get(i - 1),
                            ordered.get(i)
                    ),
                    "Consecutive team appearance at match "
                            + (i + 1)
            );
        }
    }

    // =========================================================
    // 4. RANDOMIZATION TESTS
    // =========================================================

    @Test
    void shouldGenerateSameOrderForSameSeed() {

        List<TournamentTeam> teams = createTeams(6);

        List<RoundRobinMatch> fixtures =
                roundRobinGenerator.generate(teams, false);

        List<RoundRobinMatch> first =
                orderingEngine.arrange(fixtures, 12345L);

        List<RoundRobinMatch> second =
                orderingEngine.arrange(fixtures, 12345L);

        assertEquals(first, second);
    }

    @Test
    void shouldHandleDifferentRandomSeeds() {

        List<TournamentTeam> teams = createTeams(6);

        List<RoundRobinMatch> fixtures =
                roundRobinGenerator.generate(teams, false);

        for (long seed = 1; seed <= 20; seed++) {

            List<RoundRobinMatch> ordered =
                    orderingEngine.arrange(fixtures, seed);

            assertEquals(
                    fixtures.size(),
                    ordered.size()
            );

            assertEquals(
                    new HashSet<>(fixtures),
                    new HashSet<>(ordered)
            );

            for (int i = 1; i < ordered.size(); i++) {

                assertFalse(
                        sharesTeam(
                                ordered.get(i - 1),
                                ordered.get(i)
                        )
                );
            }
        }
    }

    // =========================================================
    // 5. IMPOSSIBLE SCHEDULING TESTS
    // =========================================================

    @Test
    void shouldRejectImpossibleConsecutiveMatchConstraint() {

        List<TournamentTeam> teams = createTeams(3);

        List<RoundRobinMatch> fixtures =
                roundRobinGenerator.generate(teams, false);

        assertThrows(
                IllegalStateException.class,
                () -> orderingEngine.arrange(fixtures, 12345L)
        );
    }

    // =========================================================
    // 6. EXTENDED STRESS TESTS
    // =========================================================

    @Test
    void shouldHandleMultipleTeamCountsAndRandomSeeds() {

        // Four-team single round robin cannot satisfy
        // the no-consecutive-appearances constraint.
        int[] teamCounts = {6, 8, 10, 12};

        for (int teamCount : teamCounts) {

            List<TournamentTeam> teams =
                    createTeams(teamCount);

            List<RoundRobinMatch> fixtures =
                    roundRobinGenerator.generate(teams, false);

            for (long seed = 1; seed <= 100; seed++) {

                final long currentSeed = seed;

                List<RoundRobinMatch> ordered =
                        assertDoesNotThrow(
                                () -> orderingEngine.arrange(
                                        fixtures,
                                        currentSeed
                                ),
                                "Ordering failed for "
                                        + teamCount
                                        + " teams with seed "
                                        + currentSeed
                        );

                assertEquals(
                        fixtures.size(),
                        ordered.size()
                );

                assertEquals(
                        new HashSet<>(fixtures),
                        new HashSet<>(ordered)
                );

                for (int i = 1; i < ordered.size(); i++) {

                    assertFalse(
                            sharesTeam(
                                    ordered.get(i - 1),
                                    ordered.get(i)
                            ),
                            "Consecutive appearance for "
                                    + teamCount
                                    + " teams, seed "
                                    + currentSeed
                    );
                }
            }
        }
    }

    // =========================================================
    // 7. HELPER METHODS
    // =========================================================

    private List<TournamentTeam> createTeams(int count) {

        List<TournamentTeam> teams = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            TournamentTeam team = new TournamentTeam();

            team.setId(UUID.randomUUID());

            teams.add(team);
        }

        return teams;
    }

    private boolean sharesTeam(
            RoundRobinMatch first,
            RoundRobinMatch second) {

        UUID firstHome = first.homeTeam().getId();
        UUID firstAway = first.awayTeam().getId();

        UUID secondHome = second.homeTeam().getId();
        UUID secondAway = second.awayTeam().getId();

        return firstHome.equals(secondHome)
                || firstHome.equals(secondAway)
                || firstAway.equals(secondHome)
                || firstAway.equals(secondAway);
    }

}
