
package com.auction.matchschedule.dto.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.auction.matchschedule.enums.SchedulingType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchScheduleCreateRequest {

	// ==========================================
	// 1. TOURNAMENT INFORMATION
	// ==========================================

	@NotNull(message = "Tournament ID is required")
	private UUID tournamentId;

	@NotBlank(message = "Schedule name is required")
	@Size(max = 150)
	private String name;

	// ==========================================
	// 2. SCHEDULING CONFIGURATION
	// ==========================================

	@NotNull(message = "Scheduling type is required")
	private SchedulingType schedulingType;

	@NotBlank(message = "League format is required")
	private String leagueFormat;

	@NotNull(message = "Opponents to skip is required")
	@Min(value = 0, message = "Opponents to skip cannot be negative")
	private Integer opponentsToSkip = 0;

	@NotBlank(message = "Playoff format is required")
	private String playoffFormat;

	@Min(value = 0, message = "Qualifying team count cannot be negative")
	private Integer qualifyingTeamCount = 0;

	// ==========================================
	// 3. TOURNAMENT DATES
	// ==========================================

	@NotNull(message = "Start date is required")
	private LocalDate startDate;

	@NotNull(message = "End date is required")
	private LocalDate endDate;

	private Set<DayOfWeek> allowedDaysOfWeek = new HashSet<>();

	private Set<LocalDate> selectedDates = new HashSet<>();

	private Set<LocalDate> excludedDates = new HashSet<>();

	// ==========================================
	// 4. DAILY MATCH TIMINGS
	// ==========================================

	@NotNull(message = "Daily start time is required")
	private LocalTime dailyStartTime;

	@NotNull(message = "Daily end time is required")
	private LocalTime dailyEndTime;

	@NotNull(message = "Match duration is required")
	@Min(value = 1, message = "Match duration must be positive")
	private Integer matchDurationMinutes;

	@Min(value = 0, message = "Break cannot be negative")
	private Integer breakBetweenMatchesMinutes = 0;

	// ==========================================
	// 5. FAIRNESS CONFIGURATION
	// ==========================================

	@Min(value = 0, message = "Minimum rest cannot be negative")
	private Integer minimumRestMinutes = 0;

	@Min(value = 0, message = "Minimum matches between appearances cannot be negative")
	private Integer minimumMatchesBetweenAppearances = 1;

	@NotNull(message = "Maximum matches per team per day is required")
	@Min(value = 1)
	private Integer maxMatchesPerTeamPerDay;

	// ==========================================
	// 6. GROUND CONFIGURATION
	// ==========================================

	@NotNull(message = "Number of grounds is required")
	@Min(value = 1)
	@Max(value = 20)
	private Integer numberOfGrounds = 1;

	// ==========================================
	// 7. RANDOMIZATION
	// ==========================================

	private Long randomSeed;

	public UUID getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(UUID tournamentId) {
		this.tournamentId = tournamentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setBreakBetweenMatchesMinutes(Integer breakBetweenMatchesMinutes) {
		this.breakBetweenMatchesMinutes = breakBetweenMatchesMinutes;
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

	public void setMinimumMatchesBetweenAppearances(Integer minimumMatchesBetweenAppearances) {
		this.minimumMatchesBetweenAppearances = minimumMatchesBetweenAppearances;
	}

	public Integer getMaxMatchesPerTeamPerDay() {
		return maxMatchesPerTeamPerDay;
	}

	public void setMaxMatchesPerTeamPerDay(Integer maxMatchesPerTeamPerDay) {
		this.maxMatchesPerTeamPerDay = maxMatchesPerTeamPerDay;
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
	
	public Integer getOpponentsToSkip() {
	    return opponentsToSkip;
	}

	public void setOpponentsToSkip(Integer opponentsToSkip) {
	    this.opponentsToSkip = opponentsToSkip;
	}
}
