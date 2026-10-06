package com.auction.cricketgroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.auction.entity.BaseEntity;
import com.auction.player.entity.Player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "cricket_group_players",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cricket_group_player",
            columnNames = {"cricket_group_id", "player_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class CricketGroupPlayer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cricket_group_id", nullable = false)
    private CricketGroup cricketGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "active", nullable = false)
    private boolean active = true;

	public CricketGroup getCricketGroup() {
		return cricketGroup;
	}

	public void setCricketGroup(CricketGroup cricketGroup) {
		this.cricketGroup = cricketGroup;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
