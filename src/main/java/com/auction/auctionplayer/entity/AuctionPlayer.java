package com.auction.auctionplayer.entity;

import java.math.BigDecimal;

import java.util.UUID;

import com.auction.auction.entity.Auction;
import com.auction.auctionplayer.enums.AuctionPlayerStatus;
import com.auction.entity.BaseEntity;
import com.auction.player.entity.Player;
import com.auction.team.entity.Team;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auction_players")
@Getter
@Setter
@NoArgsConstructor
public class AuctionPlayer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_status", nullable = false, length = 30)
    private AuctionPlayerStatus auctionStatus;

    @Column(name = "current_bid", precision = 12, scale = 2)
    private BigDecimal currentBid;

    @Column(name = "sold_price", precision = 12, scale = 2)
    private BigDecimal soldPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sold_to_team_id")
    private Team soldToTeam;

	public Auction getAuction() {
		return auction;
	}

	public void setAuction(Auction auction) {
		this.auction = auction;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public AuctionPlayerStatus getAuctionStatus() {
		return auctionStatus;
	}

	public void setAuctionStatus(AuctionPlayerStatus auctionStatus) {
		this.auctionStatus = auctionStatus;
	}

	public BigDecimal getCurrentBid() {
		return currentBid;
	}

	public void setCurrentBid(BigDecimal currentBid) {
		this.currentBid = currentBid;
	}

	public BigDecimal getSoldPrice() {
		return soldPrice;
	}

	public void setSoldPrice(BigDecimal soldPrice) {
		this.soldPrice = soldPrice;
	}

	public Team getSoldToTeam() {
		return soldToTeam;
	}

	public void setSoldToTeam(Team soldToTeam) {
		this.soldToTeam = soldToTeam;
	}
}