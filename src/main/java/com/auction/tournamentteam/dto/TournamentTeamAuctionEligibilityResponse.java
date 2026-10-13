package com.auction.tournamentteam.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class TournamentTeamAuctionEligibilityResponse {

    private UUID tournamentTeamId;
    private BigDecimal initialBudget;
    private BigDecimal remainingBudget;
    private int squadSizeLimit;
    private long playersPurchased;
    private int remainingSlots;
    private BigDecimal requiredReserve;
    private BigDecimal maximumAffordableBid;
    private boolean eligibleToBid;
    private String eligibilityMessage;

    public UUID getTournamentTeamId() {
        return tournamentTeamId;
    }

    public void setTournamentTeamId(UUID tournamentTeamId) {
        this.tournamentTeamId = tournamentTeamId;
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

    public int getSquadSizeLimit() {
        return squadSizeLimit;
    }

    public void setSquadSizeLimit(int squadSizeLimit) {
        this.squadSizeLimit = squadSizeLimit;
    }

    public long getPlayersPurchased() {
        return playersPurchased;
    }

    public void setPlayersPurchased(long playersPurchased) {
        this.playersPurchased = playersPurchased;
    }

    public int getRemainingSlots() {
        return remainingSlots;
    }

    public void setRemainingSlots(int remainingSlots) {
        this.remainingSlots = remainingSlots;
    }

    public BigDecimal getRequiredReserve() {
        return requiredReserve;
    }

    public void setRequiredReserve(BigDecimal requiredReserve) {
        this.requiredReserve = requiredReserve;
    }

    public BigDecimal getMaximumAffordableBid() {
        return maximumAffordableBid;
    }

    public void setMaximumAffordableBid(BigDecimal maximumAffordableBid) {
        this.maximumAffordableBid = maximumAffordableBid;
    }

    public boolean isEligibleToBid() {
        return eligibleToBid;
    }

    public void setEligibleToBid(boolean eligibleToBid) {
        this.eligibleToBid = eligibleToBid;
    }

    public String getEligibilityMessage() {
        return eligibilityMessage;
    }

    public void setEligibilityMessage(String eligibilityMessage) {
        this.eligibilityMessage = eligibilityMessage;
    }
}
