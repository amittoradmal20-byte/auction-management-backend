
package com.auction.matchschedule.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.auction.entity.BaseEntity;
import com.auction.matchschedule.enums.MatchScheduleStatus;
import com.auction.tournament.entity.Tournament;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "match_schedules")
public class MatchSchedule extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "tournament_id", nullable = false)
	private Tournament tournament;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "scheduling_type", nullable = false, length = 30)
	private String schedulingType;

	// ==========================================
	// CALENDAR CONFIGURATION
	// ==========================================

	@Column(name = "allowed_days_of_week", length = 100)
	private String allowedDaysOfWeek;

	@Column(name = "selected_dates", columnDefinition = "TEXT")
	private String selectedDates;

	@Column(name = "league_format", nullable = false, length = 30)
	private String leagueFormat;

	@Column(name = "playoff_format", nullable = false, length = 30)
	private String playoffFormat;

	@Column(name = "qualifying_team_count", nullable = false)
	private Integer qualifyingTeamCount = 0;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Column(name = "daily_start_time", nullable = false)
	private LocalTime dailyStartTime;

	@Column(name = "daily_end_time", nullable = false)
	private LocalTime dailyEndTime;

	@Column(name = "match_duration_minutes", nullable = false)
	private Integer matchDurationMinutes;

	@Column(name = "break_between_matches_minutes", nullable = false)
	private Integer breakBetweenMatchesMinutes = 0;

	@Column(name = "minimum_rest_minutes", nullable = false)
	private Integer minimumRestMinutes = 0;

	@Column(name = "minimum_matches_between_appearances", nullable = false)
	private Integer minimumMatchesBetweenAppearances = 1;

	@Column(name = "max_matches_per_team_per_day", nullable = false)
	private Integer maxMatchesPerTeamPerDay;

	@Column(name = "number_of_grounds", nullable = false)
	private Integer numberOfGrounds = 1;

	@Column(name = "random_seed", nullable = false)
	private Long randomSeed;

	@Column(name = "generation_number", nullable = false)
	private Integer generationNumber = 1;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private MatchScheduleStatus status = MatchScheduleStatus.DRAFT;

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchedulingType() {
		return schedulingType;
	}

	public void setSchedulingType(String schedulingType) {
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

	public Integer getGenerationNumber() {
		return generationNumber;
	}

	public void setGenerationNumber(Integer generationNumber) {
		this.generationNumber = generationNumber;
	}

	public MatchScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(MatchScheduleStatus status) {
		this.status = status;
	}

	public String getAllowedDaysOfWeek() {
		return allowedDaysOfWeek;
	}

	public void setAllowedDaysOfWeek(String allowedDaysOfWeek) {
		this.allowedDaysOfWeek = allowedDaysOfWeek;
	}

	public String getSelectedDates() {
		return selectedDates;
	}

	public void setSelectedDates(String selectedDates) {
		this.selectedDates = selectedDates;
	}

}
