
package com.auction.matchschedule.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScheduleGenerateRequest {

    // ==========================================
    // 1. RANDOMIZATION CONFIGURATION
    // ==========================================

    /**
     * Optional random seed.
     *
     * When provided, the same seed and unchanged input
     * should produce a reproducible random team draw.
     *
     * When omitted, the service will generate a new seed.
     */
    private Long randomSeed;

    // ==========================================
    // 2. GENERATION OPTIONS
    // ==========================================

    /**
     * If true, replace existing draft fixtures and team draws.
     *
     * If false, generation must reject a schedule that
     * already contains generated fixtures or team draws.
     */
    private Boolean regenerate = false;

    // ==========================================
    // 3. FAIRNESS CONFIGURATION
    // ==========================================

    /**
     * Optional override for minimum intervening matches
     * between appearances by the same team.
     *
     * Null means use the saved schedule configuration.
     */
    @Min(
        value = 0,
        message = "Minimum matches between appearances cannot be negative"
    )
    private Integer minimumMatchesBetweenAppearances;

	public Long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(Long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public Boolean getRegenerate() {
		return regenerate;
	}

	public void setRegenerate(Boolean regenerate) {
		this.regenerate = regenerate;
	}

	public Integer getMinimumMatchesBetweenAppearances() {
		return minimumMatchesBetweenAppearances;
	}

	public void setMinimumMatchesBetweenAppearances(Integer minimumMatchesBetweenAppearances) {
		this.minimumMatchesBetweenAppearances = minimumMatchesBetweenAppearances;
	}

}
