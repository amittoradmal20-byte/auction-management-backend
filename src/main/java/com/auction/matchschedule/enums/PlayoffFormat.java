package com.auction.matchschedule.enums;

import java.util.Locale;

public enum PlayoffFormat {

    // ==========================================
    // SUPPORTED PLAYOFF FORMATS
    // ==========================================

    NONE(0, 0),

    TOP_2_FINAL(2, 1),

    TOP_3_PLAYOFF(3, 2),

    TOP_4_SEMIFINAL(4, 3),

    TOP_4_PLAYOFF(4, 4);

    // ==========================================
    // PLAYOFF CONFIGURATION
    // ==========================================

    private final int qualifyingTeamCount;

    private final int playoffMatchCount;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    PlayoffFormat(
            int qualifyingTeamCount,
            int playoffMatchCount) {

        this.qualifyingTeamCount = qualifyingTeamCount;
        this.playoffMatchCount = playoffMatchCount;
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public int getQualifyingTeamCount() {
        return qualifyingTeamCount;
    }

    public int getPlayoffMatchCount() {
        return playoffMatchCount;
    }

    // ==========================================
    // PLAYOFF ENABLED CHECK
    // ==========================================

    public boolean hasPlayoffs() {
        return playoffMatchCount > 0;
    }

    // ==========================================
    // CONVERT STRING TO PLAYOFF FORMAT
    // ==========================================

    public static PlayoffFormat fromValue(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Playoff format is required"
            );
        }

        String normalizedValue =
                value.trim().toUpperCase(Locale.ROOT);

        try {

            return PlayoffFormat.valueOf(normalizedValue);

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Unsupported playoff format: " + value,
                    ex
            );
        }
    }

    // ==========================================
    // VALIDATE PARTICIPATING TEAM COUNT
    // ==========================================

    public void validateTeamCount(int totalTeams) {

        if (totalTeams < qualifyingTeamCount) {

            throw new IllegalArgumentException(
                    String.format(
                            "Playoff format %s requires at least "
                            + "%d participating teams, but only "
                            + "%d teams are available",
                            name(),
                            qualifyingTeamCount,
                            totalTeams
                    )
            );
        }
    }
}