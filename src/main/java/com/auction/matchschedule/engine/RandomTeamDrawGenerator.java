
package com.auction.matchschedule.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auction.tournamentteam.entity.TournamentTeam;
import com.auction.tournamentteam.enums.TournamentTeamStatus;
import com.auction.tournamentteam.repository.TournamentTeamRepository;

@Component
public class RandomTeamDrawGenerator {

    private final TournamentTeamRepository tournamentTeamRepository;

    public RandomTeamDrawGenerator(
            TournamentTeamRepository tournamentTeamRepository) {

        this.tournamentTeamRepository = tournamentTeamRepository;
    }

    /**
     * Generates randomized team assignments for a tournament.
     *
     * The list position determines the random team number:
     *
     * Index 0 = Team Number 1
     * Index 1 = Team Number 2
     * Index 2 = Team Number 3
     *
     * @param tournamentId Tournament UUID
     * @param randomSeed Seed used for randomization
     * @return Randomly ordered active tournament teams
     */
    public List<TournamentTeam> generateRandomDraw(
            UUID tournamentId,
            long randomSeed) {

        if (tournamentId == null) {
            throw new IllegalArgumentException(
                    "Tournament ID is required."
            );
        }

        List<TournamentTeam> activeTeams =
                tournamentTeamRepository.findByTournamentIdAndStatus(
                        tournamentId,
                        TournamentTeamStatus.ACTIVE
                );

        if (activeTeams.size() < 2) {
            throw new IllegalStateException(
                    "At least two active tournament teams are required "
                    + "to generate a match schedule."
            );
        }

        // Create a separate list so the original repository
        // result is not modified.
        List<TournamentTeam> randomizedTeams =
                new ArrayList<>(activeTeams);

        // Sort first to ensure a stable initial order.
        // This makes the same seed reproducible.
        randomizedTeams.sort(
                (team1, team2) ->
                        team1.getId().compareTo(team2.getId())
        );

        // Shuffle teams using the supplied random seed.
        Collections.shuffle(
                randomizedTeams,
                new Random(randomSeed)
        );

        return randomizedTeams;
    }

}
