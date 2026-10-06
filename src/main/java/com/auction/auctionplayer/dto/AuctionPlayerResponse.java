package com.auction.auctionplayer.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.auction.auctionplayer.enums.AuctionPlayerStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionPlayerResponse {

    private UUID id;

    private UUID auctionId;

    private UUID playerId;

    private UUID teamId;

    private BigDecimal basePrice;

    private AuctionPlayerStatus status;

    private BigDecimal currentBid;

    private BigDecimal soldPrice;

    private UUID soldToTeamId;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(UUID auctionId) {
		this.auctionId = auctionId;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}

	public UUID getTeamId() {
		return teamId;
	}

	public void setTeamId(UUID teamId) {
		this.teamId = teamId;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public AuctionPlayerStatus getStatus() {
		return status;
	}

	public void setStatus(AuctionPlayerStatus status) {
		this.status = status;
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

	public UUID getSoldToTeamId() {
		return soldToTeamId;
	}

	public void setSoldToTeamId(UUID soldToTeamId) {
		this.soldToTeamId = soldToTeamId;
	}
}
