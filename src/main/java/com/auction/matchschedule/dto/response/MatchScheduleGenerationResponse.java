
package com.auction.matchschedule.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.auction.matchschedule.enums.MatchScheduleStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScheduleGenerationResponse {

    // ==========================================
    // 1. SCHEDULE INFORMATION
    // ==========================================

    private UUID matchScheduleId;

    private UUID tournamentId;

    private String scheduleName;

    private MatchScheduleStatus status;

    // ==========================================
    // 2. GENERATION INFORMATION
    // ==========================================

    private Integer generationNumber;

    private Long randomSeed;

    private Boolean regenerated;

    // ==========================================
    // 3. MATCH STATISTICS
    // ==========================================

    private Integer totalTeams;

    private Integer totalLeagueMatches;

    private Integer totalPlayoffMatches;

    private Integer totalMatches;

    // ==========================================
    // 4. RANDOMIZED TEAM DRAWS
    // ==========================================

    private List<MatchScheduleTeamDrawResponse> teamDraws =
            new ArrayList<>();

    // ==========================================
    // 5. GENERATED FIXTURES
    // ==========================================

    private List<MatchScheduleFixtureResponse> fixtures =
            new ArrayList<>();

    // ==========================================
    // 6. GENERATION TIMESTAMP
    // ==========================================

    private LocalDateTime generatedAt;

	public UUID getMatchScheduleId() {
		return matchScheduleId;
	}

	public void setMatchScheduleId(UUID matchScheduleId) {
		this.matchScheduleId = matchScheduleId;
	}

	public UUID getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(UUID tournamentId) {
		this.tournamentId = tournamentId;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public MatchScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(MatchScheduleStatus status) {
		this.status = status;
	}

	public Integer getGenerationNumber() {
		return generationNumber;
	}

	public void setGenerationNumber(Integer generationNumber) {
		this.generationNumber = generationNumber;
	}

	public Long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(Long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public Boolean getRegenerated() {
		return regenerated;
	}

	public void setRegenerated(Boolean regenerated) {
		this.regenerated = regenerated;
	}

	public Integer getTotalTeams() {
		return totalTeams;
	}

	public void setTotalTeams(Integer totalTeams) {
		this.totalTeams = totalTeams;
	}

	public Integer getTotalLeagueMatches() {
		return totalLeagueMatches;
	}

	public void setTotalLeagueMatches(Integer totalLeagueMatches) {
		this.totalLeagueMatches = totalLeagueMatches;
	}

	public Integer getTotalPlayoffMatches() {
		return totalPlayoffMatches;
	}

	public void setTotalPlayoffMatches(Integer totalPlayoffMatches) {
		this.totalPlayoffMatches = totalPlayoffMatches;
	}

	public Integer getTotalMatches() {
		return totalMatches;
	}

	public void setTotalMatches(Integer totalMatches) {
		this.totalMatches = totalMatches;
	}

	public List<MatchScheduleTeamDrawResponse> getTeamDraws() {
		return teamDraws;
	}

	public void setTeamDraws(List<MatchScheduleTeamDrawResponse> teamDraws) {
		this.teamDraws = teamDraws;
	}

	public List<MatchScheduleFixtureResponse> getFixtures() {
		return fixtures;
	}

	public void setFixtures(List<MatchScheduleFixtureResponse> fixtures) {
		this.fixtures = fixtures;
	}

	public LocalDateTime getGeneratedAt() {
		return generatedAt;
	}

	public void setGeneratedAt(LocalDateTime generatedAt) {
		this.generatedAt = generatedAt;
	}

}
