package com.auction.tournamentteam.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.auction.tournamentteam.enums.TournamentTeamStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentTeamResponse {

    private UUID id;

    private UUID tournamentId;

    private UUID teamId;

    private UUID ownerId;

    private BigDecimal initialBudget;

    private BigDecimal remainingBudget;

    private TournamentTeamStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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

	public BigDecimal getRemainingBudget() {
		return remainingBudget;
	}

	public void setRemainingBudget(BigDecimal remainingBudget) {
		this.remainingBudget = remainingBudget;
	}

	public TournamentTeamStatus getStatus() {
		return status;
	}

	public void setStatus(TournamentTeamStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}