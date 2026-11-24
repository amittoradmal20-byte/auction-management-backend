
package com.auction.matchschedule.entity;

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
    name = "match_schedule_team_draws",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_match_schedule_draw_team",
            columnNames = {
                "match_schedule_id",
                "tournament_team_id"
            }
        ),
        @UniqueConstraint(
            name = "uk_match_schedule_draw_number",
            columnNames = {
                "match_schedule_id",
                "random_number"
            }
        )
    }
)
public class MatchScheduleTeamDraw extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "match_schedule_id",
        nullable = false
    )
    private MatchSchedule matchSchedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "tournament_team_id",
        nullable = false
    )
    private TournamentTeam tournamentTeam;

    @Column(
        name = "random_number",
        nullable = false
    )
    private Integer randomNumber;

	public MatchSchedule getMatchSchedule() {
		return matchSchedule;
	}

	public void setMatchSchedule(MatchSchedule matchSchedule) {
		this.matchSchedule = matchSchedule;
	}

	public TournamentTeam getTournamentTeam() {
		return tournamentTeam;
	}

	public void setTournamentTeam(TournamentTeam tournamentTeam) {
		this.tournamentTeam = tournamentTeam;
	}

	public Integer getRandomNumber() {
		return randomNumber;
	}

	public void setRandomNumber(Integer randomNumber) {
		this.randomNumber = randomNumber;
	}
    
}
