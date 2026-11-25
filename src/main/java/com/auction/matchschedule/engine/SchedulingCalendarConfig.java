
package com.auction.matchschedule.engine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.auction.matchschedule.enums.SchedulingType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulingCalendarConfig {

    // ==========================================
    // SCHEDULING TYPE
    // ==========================================

    private SchedulingType schedulingType;

    // ==========================================
    // TOURNAMENT DATE RANGE
    // ==========================================

    private LocalDate startDate;

    private LocalDate endDate;

    // ==========================================
    // ALLOWED PLAYING DAYS
    // ==========================================

    // Used for WEEKLY and WEEKEND scheduling.
    // Example: SATURDAY, SUNDAY.
    private Set<DayOfWeek> allowedDaysOfWeek =
            new HashSet<>();

    // Used for CUSTOM, TWO_DAY and THREE_DAY
    // scheduling when specific dates are selected.
    private Set<LocalDate> selectedDates =
            new HashSet<>();

    // Dates that must never be used.
    private Set<LocalDate> excludedDates =
            new HashSet<>();

    // ==========================================
    // DAILY MATCH TIMINGS
    // ==========================================

    private LocalTime dailyStartTime;

    private LocalTime dailyEndTime;

    // Duration of one match.
    private Integer matchDurationMinutes;

    // Gap between matches on the same ground.
    private Integer breakBetweenMatchesMinutes = 0;

    // Minimum time between a team's matches.
    private Integer minimumRestMinutes = 0;

    // ==========================================
    // MATCH FAIRNESS
    // ==========================================

    // Minimum number of other matches between
    // two appearances of the same team.
    private Integer minimumMatchesBetweenAppearances = 1;

    // Maximum matches a team may play per day.
    private Integer maxMatchesPerTeamPerDay;

    // ==========================================
    // GROUND CONFIGURATION
    // ==========================================

    private Integer numberOfGrounds = 1;

	public SchedulingType getSchedulingType() {
		return schedulingType;
	}

	public void setSchedulingType(SchedulingType schedulingType) {
		this.schedulingType = schedulingType;
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
    
    

}
