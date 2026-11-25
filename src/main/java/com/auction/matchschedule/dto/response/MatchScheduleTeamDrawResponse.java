
package com.auction.matchschedule.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScheduleTeamDrawResponse {

    // ==========================================
    // 1. TEAM DRAW INFORMATION
    // ==========================================

    private UUID id;

    private UUID matchScheduleId;

    // ==========================================
    // 2. TOURNAMENT TEAM INFORMATION
    // ==========================================

    private UUID tournamentTeamId;

    private UUID teamId;

    private String teamName;

    private String teamShortName;

    // ==========================================
    // 3. RANDOM DRAW INFORMATION
    // ==========================================

    private Integer randomNumber;

    // ==========================================
    // 4. AUDIT INFORMATION
    // ==========================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getMatchScheduleId() {
		return matchScheduleId;
	}

	public void setMatchScheduleId(UUID matchScheduleId) {
		this.matchScheduleId = matchScheduleId;
	}

	public UUID getTournamentTeamId() {
		return tournamentTeamId;
	}

	public void setTournamentTeamId(UUID tournamentTeamId) {
		this.tournamentTeamId = tournamentTeamId;
	}

	public UUID getTeamId() {
		return teamId;
	}

	public void setTeamId(UUID teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamShortName() {
		return teamShortName;
	}

	public void setTeamShortName(String teamShortName) {
		this.teamShortName = teamShortName;
	}

	public Integer getRandomNumber() {
		return randomNumber;
	}

	public void setRandomNumber(Integer randomNumber) {
		this.randomNumber = randomNumber;
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
