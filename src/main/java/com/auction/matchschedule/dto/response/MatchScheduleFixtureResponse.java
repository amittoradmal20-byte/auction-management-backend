
package com.auction.matchschedule.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScheduleFixtureResponse {

    // ==========================================
    // 1. FIXTURE INFORMATION
    // ==========================================

    private UUID id;

    private UUID matchScheduleId;

    private Integer matchNumber;

    private Integer roundNumber;

    private String matchStage;

    private String status;

    // ==========================================
    // 2. HOME TEAM INFORMATION
    // ==========================================

    private UUID homeTournamentTeamId;

    private String homeTeamName;

    private String homeTeamShortName;

    private String homeTeamLogoUrl;

    private String homeTeamSource;

    // ==========================================
    // 3. AWAY TEAM INFORMATION
    // ==========================================

    private UUID awayTournamentTeamId;

    private String awayTeamName;

    private String awayTeamShortName;

    private String awayTeamLogoUrl;

    private String awayTeamSource;

    // ==========================================
    // 4. MATCH DATE AND TIME
    // ==========================================

    private LocalDate matchDate;

    private LocalTime startTime;

    private LocalTime endTime;

    // ==========================================
    // 5. GROUND INFORMATION
    // ==========================================

    private Integer groundNumber;

    // ==========================================
    // 6. AUDIT INFORMATION
    // ==========================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

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

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getMatchStage() {
        return matchStage;
    }

    public void setMatchStage(String matchStage) {
        this.matchStage = matchStage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ==========================================
    // HOME TEAM GETTERS AND SETTERS
    // ==========================================

    public UUID getHomeTournamentTeamId() {
        return homeTournamentTeamId;
    }

    public void setHomeTournamentTeamId(UUID homeTournamentTeamId) {
        this.homeTournamentTeamId = homeTournamentTeamId;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getHomeTeamShortName() {
        return homeTeamShortName;
    }

    public void setHomeTeamShortName(String homeTeamShortName) {
        this.homeTeamShortName = homeTeamShortName;
    }

    public String getHomeTeamLogoUrl() {
        return homeTeamLogoUrl;
    }

    public void setHomeTeamLogoUrl(String homeTeamLogoUrl) {
        this.homeTeamLogoUrl = homeTeamLogoUrl;
    }

    public String getHomeTeamSource() {
        return homeTeamSource;
    }

    public void setHomeTeamSource(String homeTeamSource) {
        this.homeTeamSource = homeTeamSource;
    }

    // ==========================================
    // AWAY TEAM GETTERS AND SETTERS
    // ==========================================

    public UUID getAwayTournamentTeamId() {
        return awayTournamentTeamId;
    }

    public void setAwayTournamentTeamId(UUID awayTournamentTeamId) {
        this.awayTournamentTeamId = awayTournamentTeamId;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public String getAwayTeamShortName() {
        return awayTeamShortName;
    }

    public void setAwayTeamShortName(String awayTeamShortName) {
        this.awayTeamShortName = awayTeamShortName;
    }

    public String getAwayTeamLogoUrl() {
        return awayTeamLogoUrl;
    }

    public void setAwayTeamLogoUrl(String awayTeamLogoUrl) {
        this.awayTeamLogoUrl = awayTeamLogoUrl;
    }

    public String getAwayTeamSource() {
        return awayTeamSource;
    }

    public void setAwayTeamSource(String awayTeamSource) {
        this.awayTeamSource = awayTeamSource;
    }

    // ==========================================
    // MATCH DATE AND TIME
    // ==========================================

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    // ==========================================
    // GROUND INFORMATION
    // ==========================================

    public Integer getGroundNumber() {
        return groundNumber;
    }

    public void setGroundNumber(Integer groundNumber) {
        this.groundNumber = groundNumber;
    }

    // ==========================================
    // AUDIT INFORMATION
    // ==========================================

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
