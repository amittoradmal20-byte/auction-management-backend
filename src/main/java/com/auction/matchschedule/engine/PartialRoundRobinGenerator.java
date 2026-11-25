package com.auction.matchschedule.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.engine.RoundRobinGenerator.RoundRobinMatch;
import com.auction.tournamentteam.entity.TournamentTeam;

@Component
public class PartialRoundRobinGenerator {

    private final RoundRobinGenerator roundRobinGenerator;

    public PartialRoundRobinGenerator(
            RoundRobinGenerator roundRobinGenerator) {

        this.roundRobinGenerator = roundRobinGenerator;
    }

    // ==========================================
    // 1. GENERATE PARTIAL ROUND ROBIN
    // ==========================================

    public List<RoundRobinMatch> generate(
            List<TournamentTeam> randomizedTeams,
            Integer opponentsToSkip,
            long randomSeed) {

        validateConfiguration(
                randomizedTeams,
                opponentsToSkip
        );

        int totalTeams = randomizedTeams.size();

        int matchesPerTeam =
                totalTeams - 1 - opponentsToSkip;

        List<RoundRobinMatch> fixtures;

        if (totalTeams % 2 == 0) {

            fixtures = generateForEvenTeams(
                    randomizedTeams,
                    matchesPerTeam,
                    randomSeed
            );

        } else {

            fixtures = generateForOddTeams(
                    randomizedTeams,
                    matchesPerTeam,
                    randomSeed
            );
        }

        return Collections.unmodifiableList(fixtures);
    }

    // ==========================================
    // 2. VALIDATE CONFIGURATION
    // ==========================================

    private void validateConfiguration(
            List<TournamentTeam> teams,
            Integer opponentsToSkip) {

        if (teams == null || teams.size() < 3) {

            throw new IllegalArgumentException(
                    "At least three teams are required "
                    + "for Partial Round Robin."
            );
        }

        if (teams.stream().anyMatch(Objects::isNull)) {

            throw new IllegalArgumentException(
                    "Team list cannot contain null values."
            );
        }

        if (teams.stream().anyMatch(
                team -> team.getId() == null)) {

            throw new IllegalArgumentException(
                    "Every team must have a valid ID."
            );
        }

        long uniqueTeamCount = teams.stream()
                .map(TournamentTeam::getId)
                .distinct()
                .count();

        if (uniqueTeamCount != teams.size()) {

            throw new IllegalArgumentException(
                    "Duplicate teams are not allowed."
            );
        }

        if (opponentsToSkip == null
                || opponentsToSkip < 1) {

            throw new IllegalArgumentException(
                    "Opponents to skip must be at least 1."
            );
        }

        int totalTeams = teams.size();

        int maximumOpponentsToSkip =
                totalTeams - 2;

        if (opponentsToSkip > maximumOpponentsToSkip) {

            throw new IllegalArgumentException(
                    "Opponents to skip cannot exceed "
                    + maximumOpponentsToSkip
                    + " for " + totalTeams + " teams."
            );
        }

        int matchesPerTeam =
                totalTeams - 1 - opponentsToSkip;

        long totalAppearances =
                (long) totalTeams * matchesPerTeam;

        if (totalAppearances % 2 != 0) {

            throw new IllegalArgumentException(
                    "Invalid Partial Round Robin configuration. "
                    + totalTeams + " teams cannot each play "
                    + matchesPerTeam + " matches equally."
            );
        }
    }

    // ==========================================
    // 3. GENERATE FOR EVEN NUMBER OF TEAMS
    // ==========================================

    private List<RoundRobinMatch> generateForEvenTeams(
            List<TournamentTeam> randomizedTeams,
            int matchesPerTeam,
            long randomSeed) {

        // Generate all single round-robin fixtures.

        List<RoundRobinMatch> allFixtures =
                roundRobinGenerator.generate(
                        randomizedTeams,
                        false
                );

        // Group fixtures by round number.

        Map<Integer, List<RoundRobinMatch>> fixturesByRound =
                new TreeMap<>();

        for (RoundRobinMatch match : allFixtures) {

            fixturesByRound
                    .computeIfAbsent(
                            match.roundNumber(),
                            key -> new ArrayList<>()
                    )
                    .add(match);
        }

        // Randomize available rounds.

        List<Integer> availableRounds =
                new ArrayList<>(fixturesByRound.keySet());

        Collections.shuffle(
                availableRounds,
                new Random(randomSeed)
        );

        // Select only the required number of rounds.

        List<Integer> selectedRounds =
                availableRounds.subList(
                        0,
                        matchesPerTeam
                );

        List<RoundRobinMatch> fixtures =
                new ArrayList<>();

        int newRoundNumber = 1;

        for (Integer selectedRound : selectedRounds) {

            List<RoundRobinMatch> roundMatches =
                    fixturesByRound.get(selectedRound);

            for (RoundRobinMatch match : roundMatches) {

                fixtures.add(
                        new RoundRobinMatch(
                                newRoundNumber,
                                match.homeTeam(),
                                match.awayTeam()
                        )
                );
            }

            newRoundNumber++;
        }

        return fixtures;
    }

    // ==========================================
    // 4. GENERATE FOR ODD NUMBER OF TEAMS
    // ==========================================

    private List<RoundRobinMatch> generateForOddTeams(
            List<TournamentTeam> randomizedTeams,
            int matchesPerTeam,
            long randomSeed) {

        List<TournamentTeam> teams =
                new ArrayList<>(randomizedTeams);

        int totalTeams = teams.size();

        Random random = new Random(randomSeed);

        // Randomize circular team positions.

        Collections.shuffle(teams, random);

        // For odd team counts, matches per team
        // must always be an even number.

        int requiredOffsets = matchesPerTeam / 2;

        List<Integer> availableOffsets =
                new ArrayList<>();

        for (int offset = 1;
                offset <= (totalTeams - 1) / 2;
                offset++) {

            availableOffsets.add(offset);
        }

        Collections.shuffle(
                availableOffsets,
                random
        );

        List<Integer> selectedOffsets =
                availableOffsets.subList(
                        0,
                        requiredOffsets
                );

        // Each round must contain a team at most once.

        List<List<RoundRobinMatch>> rounds =
                new ArrayList<>();

        for (Integer offset : selectedOffsets) {

            for (int i = 0; i < totalTeams; i++) {

                int opponentIndex =
                        (i + offset) % totalTeams;

                TournamentTeam homeTeam =
                        teams.get(i);

                TournamentTeam awayTeam =
                        teams.get(opponentIndex);

                addToAvailableRound(
                        rounds,
                        homeTeam,
                        awayTeam
                );
            }
        }

        List<RoundRobinMatch> fixtures =
                new ArrayList<>();

        for (List<RoundRobinMatch> round : rounds) {

            fixtures.addAll(round);
        }

        return fixtures;
    }

    // ==========================================
    // 5. ASSIGN MATCH TO AVAILABLE ROUND
    // ==========================================

    private void addToAvailableRound(
            List<List<RoundRobinMatch>> rounds,
            TournamentTeam homeTeam,
            TournamentTeam awayTeam) {

        for (int i = 0; i < rounds.size(); i++) {

            List<RoundRobinMatch> round =
                    rounds.get(i);

            boolean teamAlreadyPlaying =
                    round.stream().anyMatch(match ->

                            match.homeTeam().getId()
                                    .equals(homeTeam.getId())

                            || match.awayTeam().getId()
                                    .equals(homeTeam.getId())

                            || match.homeTeam().getId()
                                    .equals(awayTeam.getId())

                            || match.awayTeam().getId()
                                    .equals(awayTeam.getId())
                    );

            if (!teamAlreadyPlaying) {

                round.add(
                        new RoundRobinMatch(
                                i + 1,
                                homeTeam,
                                awayTeam
                        )
                );

                return;
            }
        }

        // No existing round is available.
        // Create a new round.

        List<RoundRobinMatch> newRound =
                new ArrayList<>();

        newRound.add(
                new RoundRobinMatch(
                        rounds.size() + 1,
                        homeTeam,
                        awayTeam
                )
        );

        rounds.add(newRound);
    }
}