
package com.auction.matchschedule.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.auction.tournamentteam.entity.TournamentTeam;

@Component
public class RoundRobinGenerator {

    /**
     * Generates single or double round-robin fixtures.
     *
     * @param randomizedTeams Teams ordered by their random draw
     * @param doubleRoundRobin true = each pairing twice
     * @return Round-wise fixtures without dates or times
     */
    public List<RoundRobinMatch> generate(
            List<TournamentTeam> randomizedTeams,
            boolean doubleRoundRobin) {

        if (randomizedTeams == null
                || randomizedTeams.size() < 2) {

            throw new IllegalArgumentException(
                    "At least two teams are required."
            );
        }

        if (randomizedTeams.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Team list cannot contain null values."
            );
        }

        long uniqueTeamCount = randomizedTeams.stream()
                .map(TournamentTeam::getId)
                .distinct()
                .count();

        if (uniqueTeamCount != randomizedTeams.size()) {
            throw new IllegalArgumentException(
                    "Duplicate teams are not allowed."
            );
        }

        List<TournamentTeam> rotation =
                new ArrayList<>(randomizedTeams);

        // A BYE is represented by null for odd team counts.
        if (rotation.size() % 2 != 0) {
            rotation.add(null);
        }

        int teamSlots = rotation.size();
        int rounds = teamSlots - 1;
        int matchesPerRound = teamSlots / 2;

        List<RoundRobinMatch> fixtures = new ArrayList<>();

        // ==========================================
        // FIRST LEG
        // ==========================================

        for (int round = 1; round <= rounds; round++) {

            for (int i = 0; i < matchesPerRound; i++) {

                TournamentTeam home = rotation.get(i);

                TournamentTeam away =
                        rotation.get(teamSlots - 1 - i);

                // Skip BYE matches.
                if (home == null || away == null) {
                    continue;
                }

                // Alternate home/away designation.
                if (round % 2 == 0) {
                    TournamentTeam temp = home;
                    home = away;
                    away = temp;
                }

                fixtures.add(
                        new RoundRobinMatch(
                                round,
                                home,
                                away
                        )
                );
            }

            // Circle method:
            // Keep the first slot fixed and rotate the others.
            TournamentTeam last =
                    rotation.remove(teamSlots - 1);

            rotation.add(1, last);
        }

        // ==========================================
        // SECOND LEG (OPTIONAL)
        // ==========================================

        if (doubleRoundRobin) {

            List<RoundRobinMatch> firstLeg =
                    new ArrayList<>(fixtures);

            for (RoundRobinMatch match : firstLeg) {

                fixtures.add(
                        new RoundRobinMatch(
                                match.roundNumber() + rounds,
                                match.awayTeam(),
                                match.homeTeam()
                        )
                );
            }
        }

        return Collections.unmodifiableList(fixtures);
    }

    /**
     * Temporary match pairing.
     *
     * Dates, times, grounds, and match numbers
     * will be assigned by the scheduling engine.
     */
    public record RoundRobinMatch(
            int roundNumber,
            TournamentTeam homeTeam,
            TournamentTeam awayTeam
    ) {
    }

}
