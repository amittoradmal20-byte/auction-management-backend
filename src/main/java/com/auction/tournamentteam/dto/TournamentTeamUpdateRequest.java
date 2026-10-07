package com.auction.tournamentteam.dto;

import com.auction.tournamentteam.enums.TournamentTeamStatus;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TournamentTeamUpdateRequest {

    @NotNull(message = "Tournament team status is required")
    private TournamentTeamStatus status;

	public TournamentTeamStatus getStatus() {
		return status;
	}

	public void setStatus(TournamentTeamStatus status) {
		this.status = status;
	}
}