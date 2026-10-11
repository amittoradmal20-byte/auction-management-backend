package com.auction.tournamentplayer.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TournamentPlayerCreateRequest {

    @NotNull(message = "Tournament ID is required")
    private UUID tournamentId;

    @NotNull(message = "Player ID is required")
    private UUID playerId;

	public UUID getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(UUID tournamentId) {
		this.tournamentId = tournamentId;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}
}
