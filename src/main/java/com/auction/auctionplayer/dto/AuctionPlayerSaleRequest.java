package com.auction.auctionplayer.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class AuctionPlayerSaleRequest {

    @NotNull(message = "Tournament team ID is required")
    private UUID tournamentTeamId;

    @NotNull(message = "Sold price is required")
    @DecimalMin(
            value = "0.01",
            message = "Sold price must be greater than zero"
    )
    private BigDecimal soldPrice;

    public UUID getTournamentTeamId() {
        return tournamentTeamId;
    }

    public void setTournamentTeamId(UUID tournamentTeamId) {
        this.tournamentTeamId = tournamentTeamId;
    }

    public BigDecimal getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(BigDecimal soldPrice) {
        this.soldPrice = soldPrice;
    }
}
