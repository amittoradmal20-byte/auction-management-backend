package com.auction.tournamentteam.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TournamentTeamCreateRequest {

    @NotNull(message = "Tournament ID is required")
    private UUID tournamentId;

    @NotNull(message = "Team ID is required")
    private UUID teamId;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @NotNull(message = "Initial budget is required")
    @DecimalMin(
        value = "0.01",
        message = "Initial budget must be greater than 0"
    )
    private BigDecimal initialBudget;

	public UUID getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(UUID tournamentId) {
		this.tournamentId = tournamentId;
	}

	public UUID getTeamId() {
		return teamId;
	}

	public void setTeamId(UUID teamId) {
		this.teamId = teamId;
	}

	public UUID getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
	}

	public BigDecimal getInitialBudget() {
		return initialBudget;
	}

	public void setInitialBudget(BigDecimal initialBudget) {
		this.initialBudget = initialBudget;
	}
}