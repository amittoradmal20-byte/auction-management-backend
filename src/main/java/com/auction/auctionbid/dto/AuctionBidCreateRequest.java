package com.auction.auctionbid.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class AuctionBidCreateRequest {

    @NotNull(message = "Tournament team ID is required")
    private UUID tournamentTeamId;

    @NotNull(message = "Bid amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Bid amount must be greater than zero")
    private BigDecimal bidAmount;

    public UUID getTournamentTeamId() {
        return tournamentTeamId;
    }

    public void setTournamentTeamId(UUID tournamentTeamId) {
        this.tournamentTeamId = tournamentTeamId;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }
}
