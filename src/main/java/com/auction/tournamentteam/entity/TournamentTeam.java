package com.auction.tournamentteam.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.auction.entity.BaseEntity;
import com.auction.entity.UserAccount;
import com.auction.team.entity.Team;
import com.auction.tournament.entity.Tournament;
import com.auction.tournamentteam.enums.TournamentTeamStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "tournament_teams",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_tournament_teams_tournament_team",
            columnNames = {"tournament_id", "team_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class TournamentTeam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "tournament_id",
        nullable = false
    )
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "team_id",
        nullable = false
    )
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "owner_id",
        nullable = false
    )
    private UserAccount owner;

    @Column(
        name = "initial_budget",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal initialBudget;

    @Column(
        name = "remaining_budget",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal remainingBudget;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    private TournamentTeamStatus status;

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public UserAccount getOwner() {
		return owner;
	}

	public void setOwner(UserAccount owner) {
		this.owner = owner;
	}

	public BigDecimal getInitialBudget() {
		return initialBudget;
	}

	public void setInitialBudget(BigDecimal initialBudget) {
		this.initialBudget = initialBudget;
	}

	public BigDecimal getRemainingBudget() {
		return remainingBudget;
	}

	public void setRemainingBudget(BigDecimal remainingBudget) {
		this.remainingBudget = remainingBudget;
	}

	public TournamentTeamStatus getStatus() {
		return status;
	}

	public void setStatus(TournamentTeamStatus status) {
		this.status = status;
	}
}