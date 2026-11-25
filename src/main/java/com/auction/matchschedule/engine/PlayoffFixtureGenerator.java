package com.auction.matchschedule.engine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.enums.MatchStage;
import com.auction.matchschedule.enums.PlayoffFormat;

@Component
public class PlayoffFixtureGenerator {

    // ==========================================
    // GENERATE PLAYOFF FIXTURES
    // ==========================================

    public List<PlayoffMatch> generate(
            PlayoffFormat playoffFormat,
            int lastLeagueMatchNumber,
            int lastLeagueRoundNumber) {

        // ==========================================
        // VALIDATE INPUT
        // ==========================================

        if (playoffFormat == null) {

            throw new IllegalArgumentException(
                    "Playoff format is required."
            );
        }

        if (lastLeagueMatchNumber < 0) {

            throw new IllegalArgumentException(
                    "Last league match number cannot be negative."
            );
        }

        if (lastLeagueRoundNumber < 0) {

            throw new IllegalArgumentException(
                    "Last league round number cannot be negative."
            );
        }

        // No playoff fixtures required.

        if (!playoffFormat.hasPlayoffs()) {
            return List.of();
        }

        if (lastLeagueMatchNumber == 0
                || lastLeagueRoundNumber == 0) {

            throw new IllegalArgumentException(
                    "League fixtures are required before playoffs."
            );
        }

        List<PlayoffMatch> matches = new ArrayList<>();

        int nextMatchNumber = lastLeagueMatchNumber + 1;

        int nextRoundNumber = lastLeagueRoundNumber + 1;

        // ==========================================
        // GENERATE BASED ON PLAYOFF FORMAT
        // ==========================================

        switch (playoffFormat) {

            case NONE -> {
                // No playoff fixtures.
            }

            // ======================================
            // TOP 2 FINAL
            // ======================================

            case TOP_2_FINAL -> {

                matches.add(new PlayoffMatch(
                        nextMatchNumber,
                        nextRoundNumber,
                        MatchStage.FINAL,
                        rankSource(1),
                        rankSource(2)
                ));
            }

            // ======================================
            // TOP 3 PLAYOFF
            // ======================================

            case TOP_3_PLAYOFF -> {

                int semifinalMatchNumber = nextMatchNumber++;

                matches.add(new PlayoffMatch(
                        semifinalMatchNumber,
                        nextRoundNumber,
                        MatchStage.SEMIFINAL,
                        rankSource(2),
                        rankSource(3)
                ));

                matches.add(new PlayoffMatch(
                        nextMatchNumber,
                        nextRoundNumber + 1,
                        MatchStage.FINAL,
                        rankSource(1),
                        winnerSource(semifinalMatchNumber)
                ));
            }

            // ======================================
            // TOP 4 SEMIFINAL
            // ======================================

            case TOP_4_SEMIFINAL -> {

                int semifinal1MatchNumber = nextMatchNumber++;

                int semifinal2MatchNumber = nextMatchNumber++;

                matches.add(new PlayoffMatch(
                        semifinal1MatchNumber,
                        nextRoundNumber,
                        MatchStage.SEMIFINAL_1,
                        rankSource(1),
                        rankSource(4)
                ));

                matches.add(new PlayoffMatch(
                        semifinal2MatchNumber,
                        nextRoundNumber,
                        MatchStage.SEMIFINAL_2,
                        rankSource(2),
                        rankSource(3)
                ));

                matches.add(new PlayoffMatch(
                        nextMatchNumber,
                        nextRoundNumber + 1,
                        MatchStage.FINAL,
                        winnerSource(semifinal1MatchNumber),
                        winnerSource(semifinal2MatchNumber)
                ));
            }

            // ======================================
            // TOP 4 PLAYOFF - IPL STYLE
            // ======================================

            case TOP_4_PLAYOFF -> {

                int qualifier1MatchNumber = nextMatchNumber++;

                int eliminatorMatchNumber = nextMatchNumber++;

                int qualifier2MatchNumber = nextMatchNumber++;

                // Qualifier 1:
                // Rank 1 vs Rank 2

                matches.add(new PlayoffMatch(
                        qualifier1MatchNumber,
                        nextRoundNumber,
                        MatchStage.QUALIFIER_1,
                        rankSource(1),
                        rankSource(2)
                ));

                // Eliminator:
                // Rank 3 vs Rank 4

                matches.add(new PlayoffMatch(
                        eliminatorMatchNumber,
                        nextRoundNumber,
                        MatchStage.ELIMINATOR,
                        rankSource(3),
                        rankSource(4)
                ));

                // Qualifier 2:
                // Loser of Qualifier 1
                // vs Winner of Eliminator

                matches.add(new PlayoffMatch(
                        qualifier2MatchNumber,
                        nextRoundNumber + 1,
                        MatchStage.QUALIFIER_2,
                        loserSource(qualifier1MatchNumber),
                        winnerSource(eliminatorMatchNumber)
                ));

                // Final:
                // Winner of Qualifier 1
                // vs Winner of Qualifier 2

                matches.add(new PlayoffMatch(
                        nextMatchNumber,
                        nextRoundNumber + 2,
                        MatchStage.FINAL,
                        winnerSource(qualifier1MatchNumber),
                        winnerSource(qualifier2MatchNumber)
                ));
            }
        }

        // ==========================================
        // VALIDATE GENERATED MATCH COUNT
        // ==========================================

        if (matches.size()
                != playoffFormat.getPlayoffMatchCount()) {

            throw new IllegalStateException(
                    "Generated playoff match count does not "
                    + "match the configured playoff format: "
                    + playoffFormat.name()
            );
        }

        return List.copyOf(matches);
    }

    // ==========================================
    // TEAM SOURCE HELPERS
    // ==========================================

    private String rankSource(int rank) {

        return "RANK_" + rank;
    }

    private String winnerSource(int matchNumber) {

        return "WINNER_MATCH_" + matchNumber;
    }

    private String loserSource(int matchNumber) {

        return "LOSER_MATCH_" + matchNumber;
    }

    // ==========================================
    // PLAYOFF MATCH RECORD
    // ==========================================

    public record PlayoffMatch(

            int matchNumber,

            int roundNumber,

            MatchStage matchStage,

            String homeTeamSource,

            String awayTeamSource

    ) {
    }
}