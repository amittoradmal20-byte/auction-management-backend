package com.auction.team.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCreateRequest {

    @NotNull(message = "Tournament ID is required")
    private UUID tournamentId;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @NotBlank(message = "Team name is required")
    @Size(max = 100, message = "Team name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Short name is required")
    @Size(max = 20, message = "Short name must not exceed 20 characters")
    private String shortName;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;

    @NotNull(message = "Initial budget is required")
    @DecimalMin(
        value = "0.01",
        message = "Initial budget must be greater than zero"
    )
    private BigDecimal initialBudget;

	public UUID getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(UUID tournamentId) {
		this.tournamentId = tournamentId;
	}

	public UUID getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public BigDecimal getInitialBudget() {
		return initialBudget;
	}

	public void setInitialBudget(BigDecimal initialBudget) {
		this.initialBudget = initialBudget;
	}
}