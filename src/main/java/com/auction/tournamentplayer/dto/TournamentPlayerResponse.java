package com.auction.tournamentplayer.dto;

import java.util.UUID;

import com.auction.tournamentplayer.enums.TournamentPlayerStatus;

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
public class TournamentPlayerResponse {

    private UUID id;

    private UUID tournamentId;

    private UUID playerId;

    private TournamentPlayerStatus status;

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

	public UUID getPlayerId() {
		return playerId;
	}

	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}

	public TournamentPlayerStatus getStatus() {
		return status;
	}

	public void setStatus(TournamentPlayerStatus status) {
		this.status = status;
	}
}
