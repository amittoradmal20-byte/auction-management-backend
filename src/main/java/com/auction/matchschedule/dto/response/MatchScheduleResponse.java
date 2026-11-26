
package com.auction.matchschedule.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.auction.matchschedule.enums.MatchScheduleStatus;
import com.auction.matchschedule.enums.SchedulingType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScheduleResponse {

    // ==========================================
    // 1. SCHEDULE INFORMATION
    // ==========================================

    private UUID id;

    private UUID tournamentId;

    private String tournamentName;

    private String tournamentLogoUrl;

    private String name;

    private MatchScheduleStatus status;

    // ==========================================
    // 2. SCHEDULING CONFIGURATION
    // ==========================================

    private SchedulingType schedulingType;

    private String leagueFormat;

    private Integer opponentsToSkip;

    private String playoffFormat;

    private Integer qualifyingTeamCount;

    // ==========================================
    // 3. TOURNAMENT DATES
    // ==========================================

    private LocalDate startDate;

    private LocalDate endDate;

    private Set<DayOfWeek> allowedDaysOfWeek = new HashSet<>();

    private Set<LocalDate> selectedDates = new HashSet<>();

    private Set<LocalDate> excludedDates = new HashSet<>();

    // ==========================================
    // 4. DAILY MATCH TIMINGS
    // ==========================================

    private LocalTime dailyStartTime;

    private LocalTime dailyEndTime;

    private Integer matchDurationMinutes;

    private Integer breakBetweenMatchesMinutes;

    // ==========================================
    // 5. FAIRNESS CONFIGURATION
    // ==========================================

    private Integer minimumRestMinutes;

    private Integer minimumMatchesBetweenAppearances;

    private Integer maxMatchesPerTeamPerDay;

    // ==========================================
    // 6. GROUND CONFIGURATION
    // ==========================================

    private Integer numberOfGrounds;

    // ==========================================
    // 7. RANDOMIZATION
    // ==========================================

    private Long randomSeed;

    private Integer generationNumber;

    // ==========================================
    // 8. GENERATED MATCH STATISTICS
    // ==========================================

    private Integer totalTeams;

    private Integer totalLeagueMatches;

    private Integer totalPlayoffMatches;

    private Integer totalMatches;

    // ==========================================
    // 9. AUDIT INFORMATION
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

    public UUID getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(UUID tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getTournamentLogoUrl() {
        return tournamentLogoUrl;
    }

    public void setTournamentLogoUrl(String tournamentLogoUrl) {
        this.tournamentLogoUrl = tournamentLogoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MatchScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(MatchScheduleStatus status) {
        this.status = status;
    }

    public SchedulingType getSchedulingType() {
        return schedulingType;
    }

    public void setSchedulingType(SchedulingType schedulingType) {
        this.schedulingType = schedulingType;
    }

    public String getLeagueFormat() {
        return leagueFormat;
    }

    public void setLeagueFormat(String leagueFormat) {
        this.leagueFormat = leagueFormat;
    }

    public Integer getOpponentsToSkip() {
        return opponentsToSkip;
    }

    public void setOpponentsToSkip(Integer opponentsToSkip) {
        this.opponentsToSkip = opponentsToSkip;
    }

    public String getPlayoffFormat() {
        return playoffFormat;
    }

    public void setPlayoffFormat(String playoffFormat) {
        this.playoffFormat = playoffFormat;
    }

    public Integer getQualifyingTeamCount() {
        return qualifyingTeamCount;
    }

    public void setQualifyingTeamCount(Integer qualifyingTeamCount) {
        this.qualifyingTeamCount = qualifyingTeamCount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<DayOfWeek> getAllowedDaysOfWeek() {
        return allowedDaysOfWeek;
    }

    public void setAllowedDaysOfWeek(Set<DayOfWeek> allowedDaysOfWeek) {
        this.allowedDaysOfWeek = allowedDaysOfWeek;
    }

    public Set<LocalDate> getSelectedDates() {
        return selectedDates;
    }

    public void setSelectedDates(Set<LocalDate> selectedDates) {
        this.selectedDates = selectedDates;
    }

    public Set<LocalDate> getExcludedDates() {
        return excludedDates;
    }

    public void setExcludedDates(Set<LocalDate> excludedDates) {
        this.excludedDates = excludedDates;
    }

    public LocalTime getDailyStartTime() {
        return dailyStartTime;
    }

    public void setDailyStartTime(LocalTime dailyStartTime) {
        this.dailyStartTime = dailyStartTime;
    }

    public LocalTime getDailyEndTime() {
        return dailyEndTime;
    }

    public void setDailyEndTime(LocalTime dailyEndTime) {
        this.dailyEndTime = dailyEndTime;
    }

    public Integer getMatchDurationMinutes() {
        return matchDurationMinutes;
    }

    public void setMatchDurationMinutes(Integer matchDurationMinutes) {
        this.matchDurationMinutes = matchDurationMinutes;
    }

    public Integer getBreakBetweenMatchesMinutes() {
        return breakBetweenMatchesMinutes;
    }

    public void setBreakBetweenMatchesMinutes(
            Integer breakBetweenMatchesMinutes) {

        this.breakBetweenMatchesMinutes =
                breakBetweenMatchesMinutes;
    }

    public Integer getMinimumRestMinutes() {
        return minimumRestMinutes;
    }

    public void setMinimumRestMinutes(Integer minimumRestMinutes) {
        this.minimumRestMinutes = minimumRestMinutes;
    }

    public Integer getMinimumMatchesBetweenAppearances() {
        return minimumMatchesBetweenAppearances;
    }

    public void setMinimumMatchesBetweenAppearances(
            Integer minimumMatchesBetweenAppearances) {

        this.minimumMatchesBetweenAppearances =
                minimumMatchesBetweenAppearances;
    }

    public Integer getMaxMatchesPerTeamPerDay() {
        return maxMatchesPerTeamPerDay;
    }

    public void setMaxMatchesPerTeamPerDay(
            Integer maxMatchesPerTeamPerDay) {

        this.maxMatchesPerTeamPerDay =
                maxMatchesPerTeamPerDay;
    }

    public Integer getNumberOfGrounds() {
        return numberOfGrounds;
    }

    public void setNumberOfGrounds(Integer numberOfGrounds) {
        this.numberOfGrounds = numberOfGrounds;
    }

    public Long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(Long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public Integer getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(Integer generationNumber) {
        this.generationNumber = generationNumber;
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
