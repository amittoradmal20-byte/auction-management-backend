package com.auction.auctionplayer.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionPlayerCreateRequest {

    @NotNull(message = "Auction ID is required")
    private UUID auctionId;

    @NotNull(message = "Player ID is required")
    private UUID playerId;

    @NotNull(message = "Base price is required")
    @DecimalMin(
        value = "0.01",
        message = "Base price must be greater than 0"
    )
    private BigDecimal basePrice;

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

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}
}