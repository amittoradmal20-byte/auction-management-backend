
package com.auction.matchschedule.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auction.matchschedule.engine.RoundRobinGenerator.RoundRobinMatch;
import com.auction.tournamentteam.entity.TournamentTeam;

class RoundRobinGeneratorTest {

    private RoundRobinGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RoundRobinGenerator();
    }

    @Test
    void shouldGenerateCorrectSingleRoundRobinForDifferentTeamCounts() {

        for (int teamCount : List.of(2, 3, 4, 5, 6, 8, 10, 12)) {

            List<TournamentTeam> teams = createTeams(teamCount);

            List<RoundRobinMatch> matches =
                    generator.generate(teams, false);

            int expected = teamCount * (teamCount - 1) / 2;

            assertEquals(
                    expected,
                    matches.size(),
                    "Incorrect match count for " + teamCount + " teams"
            );

            assertUniquePairings(matches, 1);
            assertValidTeams(matches);
        }
    }

    @Test
    void shouldGenerateCorrectDoubleRoundRobinForDifferentTeamCounts() {

        for (int teamCount : List.of(2, 3, 4, 5, 6, 8, 10, 12)) {

            List<TournamentTeam> teams = createTeams(teamCount);

            List<RoundRobinMatch> matches =
                    generator.generate(teams, true);

            int expected = teamCount * (teamCount - 1);

            assertEquals(
                    expected,
                    matches.size(),
                    "Incorrect match count for " + teamCount + " teams"
            );

            assertUniquePairings(matches, 2);
            assertValidTeams(matches);
        }
    }

    @Test
    void shouldRejectFewerThanTwoTeams() {

        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(createTeams(1), false)
        );
    }

    @Test
    void shouldRejectDuplicateTeams() {

        List<TournamentTeam> teams = createTeams(2);

        teams.add(teams.get(0));

        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(teams, false)
        );
    }

    @Test
    void shouldNotModifyOriginalTeamList() {

        List<TournamentTeam> teams = createTeams(5);

        List<UUID> originalOrder = teams.stream()
                .map(TournamentTeam::getId)
                .toList();

        generator.generate(teams, false);

        assertEquals(
                originalOrder,
                teams.stream()
                        .map(TournamentTeam::getId)
                        .toList()
        );
    }

    private List<TournamentTeam> createTeams(int count) {

        List<TournamentTeam> teams = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            TournamentTeam team = new TournamentTeam();

            team.setId(UUID.randomUUID());

            teams.add(team);
        }

        return teams;
    }

    private void assertValidTeams(List<RoundRobinMatch> matches) {

        for (RoundRobinMatch match : matches) {

            assertNotNull(match.homeTeam());
            assertNotNull(match.awayTeam());

            assertNotEquals(
                    match.homeTeam().getId(),
                    match.awayTeam().getId()
            );

            assertTrue(match.roundNumber() > 0);
        }
    }

    private void assertUniquePairings(
            List<RoundRobinMatch> matches,
            int expectedOccurrences) {

        Map<Set<UUID>, Integer> pairings = new HashMap<>();

        for (RoundRobinMatch match : matches) {

            Set<UUID> pairing = new HashSet<>();

            pairing.add(match.homeTeam().getId());
            pairing.add(match.awayTeam().getId());

            pairings.merge(pairing, 1, Integer::sum);
        }

        for (Integer occurrences : pairings.values()) {

            assertEquals(
                    expectedOccurrences,
                    occurrences,
                    "Incorrect number of matches between two teams"
            );
        }
    }

}
