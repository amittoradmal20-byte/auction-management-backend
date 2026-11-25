package com.auction.matchschedule.enums;

import java.util.Locale;

public enum MatchStage {

    // ==========================================
    // LEAGUE STAGE
    // ==========================================

    LEAGUE("League", false),

    // ==========================================
    // SEMIFINAL STAGES
    // ==========================================

    SEMIFINAL("Semifinal", true),

    SEMIFINAL_1("Semifinal 1", true),

    SEMIFINAL_2("Semifinal 2", true),

    // ==========================================
    // IPL STYLE PLAYOFF STAGES
    // ==========================================

    QUALIFIER_1("Qualifier 1", true),

    ELIMINATOR("Eliminator", true),

    QUALIFIER_2("Qualifier 2", true),

    // ==========================================
    // FINAL STAGE
    // ==========================================

    FINAL("Grand Final", true);

    // ==========================================
    // MATCH STAGE CONFIGURATION
    // ==========================================

    private final String displayName;

    private final boolean playoff;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    MatchStage(
            String displayName,
            boolean playoff) {

        this.displayName = displayName;
        this.playoff = playoff;
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPlayoff() {
        return playoff;
    }

    // ==========================================
    // STAGE CHECKS
    // ==========================================

    public boolean isLeague() {
        return this == LEAGUE;
    }

    public boolean isFinal() {
        return this == FINAL;
    }

    // ==========================================
    // CONVERT STRING TO MATCH STAGE
    // ==========================================

    public static MatchStage fromValue(String value) {

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Match stage is required"
            );
        }

        String normalizedValue =
                value.trim().toUpperCase(Locale.ROOT);

        try {

            return MatchStage.valueOf(normalizedValue);

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Unsupported match stage: " + value,
                    ex
            );
        }
    }
}