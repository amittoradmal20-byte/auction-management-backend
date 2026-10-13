package com.auction.auctionbid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuctionBidResponse {

    private UUID id;
    private UUID auctionPlayerId;
    private UUID tournamentTeamId;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAuctionPlayerId() {
        return auctionPlayerId;
    }

    public void setAuctionPlayerId(UUID auctionPlayerId) {
        this.auctionPlayerId = auctionPlayerId;
    }

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

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }
}
