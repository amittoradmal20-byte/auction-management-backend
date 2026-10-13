package com.auction.auctionbid.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.auction.auctionplayer.entity.AuctionPlayer;
import com.auction.entity.BaseEntity;
import com.auction.tournamentteam.entity.TournamentTeam;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auction_bids")
@Getter
@Setter
@NoArgsConstructor
public class AuctionBid extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_player_id", nullable = false)
    private AuctionPlayer auctionPlayer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_team_id", nullable = false)
    private TournamentTeam tournamentTeam;

    @Column(name = "bid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal bidAmount;

    @Column(name = "bid_time", nullable = false)
    private LocalDateTime bidTime;

	public AuctionPlayer getAuctionPlayer() {
		return auctionPlayer;
	}

	public void setAuctionPlayer(AuctionPlayer auctionPlayer) {
		this.auctionPlayer = auctionPlayer;
	}

	public TournamentTeam getTournamentTeam() {
		return tournamentTeam;
	}

	public void setTournamentTeam(TournamentTeam tournamentTeam) {
		this.tournamentTeam = tournamentTeam;
	}

	public BigDecimal getBidAmount() {
		return bidAmount;
	}

	public void setBidAmount(BigDecimal bidAmount) {
		this.bidAmount = bidAmount;
	}

	public LocalDateTime getBidTime() {
		return bidTime;
	}

	public void setBidTime(LocalDateTime bidTime) {
		this.bidTime = bidTime;
	}
}
