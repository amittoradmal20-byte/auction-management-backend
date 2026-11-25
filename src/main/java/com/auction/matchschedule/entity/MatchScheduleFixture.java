
package com.auction.matchschedule.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.auction.entity.BaseEntity;
import com.auction.tournamentteam.entity.TournamentTeam;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "match_schedule_fixtures",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_match_fixture_number",
            columnNames = {
                "match_schedule_id",
                "match_number"
            }
        )
    }
)
public class MatchScheduleFixture extends BaseEntity {

    // ==========================================
    // MATCH SCHEDULE
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "match_schedule_id",
        nullable = false
    )
    private MatchSchedule matchSchedule;

    // ==========================================
    // MATCH DETAILS
    // ==========================================

    @Column(name = "match_number", nullable = false)
    private Integer matchNumber;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "match_stage", nullable = false, length = 30)
    private String matchStage;

    // ==========================================
    // PARTICIPATING TEAMS
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_tournament_team_id")
    private TournamentTeam homeTournamentTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_tournament_team_id")
    private TournamentTeam awayTournamentTeam;

    // Used for playoff placeholders such as:
    // RANK_1, RANK_2, WINNER_MATCH_16, LOSER_MATCH_17

    @Column(name = "home_team_source", length = 100)
    private String homeTeamSource;

    @Column(name = "away_team_source", length = 100)
    private String awayTeamSource;

    // ==========================================
    // MATCH DATE AND TIME
    // ==========================================

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // ==========================================
    // GROUND DETAILS
    // ==========================================

    @Column(name = "ground_number", nullable = false)
    private Integer groundNumber = 1;

    // ==========================================
    // FIXTURE STATUS
    // ==========================================

    @Column(name = "status", nullable = false, length = 30)
    private String status = "SCHEDULED";

	public MatchSchedule getMatchSchedule() {
		return matchSchedule;
	}

	public void setMatchSchedule(MatchSchedule matchSchedule) {
		this.matchSchedule = matchSchedule;
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

	public TournamentTeam getHomeTournamentTeam() {
		return homeTournamentTeam;
	}

	public void setHomeTournamentTeam(TournamentTeam homeTournamentTeam) {
		this.homeTournamentTeam = homeTournamentTeam;
	}

	public TournamentTeam getAwayTournamentTeam() {
		return awayTournamentTeam;
	}

	public void setAwayTournamentTeam(TournamentTeam awayTournamentTeam) {
		this.awayTournamentTeam = awayTournamentTeam;
	}

	public String getHomeTeamSource() {
		return homeTeamSource;
	}

	public void setHomeTeamSource(String homeTeamSource) {
		this.homeTeamSource = homeTeamSource;
	}

	public String getAwayTeamSource() {
		return awayTeamSource;
	}

	public void setAwayTeamSource(String awayTeamSource) {
		this.awayTeamSource = awayTeamSource;
	}

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

	public Integer getGroundNumber() {
		return groundNumber;
	}

	public void setGroundNumber(Integer groundNumber) {
		this.groundNumber = groundNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
}
