
package com.auction.dashboard.dto;

public class DashboardSummaryResponse {

    private long totalTournaments;
    private long totalCricketGroups;
    private long totalPlayers;
    private long totalTeams;
    private long totalAuctions;
    private long inProgressAuctions;
    private long pausedAuctions;

    public DashboardSummaryResponse(
            long totalTournaments,
            long totalCricketGroups,
            long totalPlayers,
            long totalTeams,
            long totalAuctions,
            long inProgressAuctions,
            long pausedAuctions) {

        this.totalTournaments = totalTournaments;
        this.totalCricketGroups = totalCricketGroups;
        this.totalPlayers = totalPlayers;
        this.totalTeams = totalTeams;
        this.totalAuctions = totalAuctions;
        this.inProgressAuctions = inProgressAuctions;
        this.pausedAuctions = pausedAuctions;
    }

    public long getTotalTournaments() {
        return totalTournaments;
    }

    public long getTotalCricketGroups() {
        return totalCricketGroups;
    }

    public long getTotalPlayers() {
        return totalPlayers;
    }

    public long getTotalTeams() {
        return totalTeams;
    }

    public long getTotalAuctions() {
        return totalAuctions;
    }

    public long getInProgressAuctions() {
        return inProgressAuctions;
    }

    public long getPausedAuctions() {
        return pausedAuctions;
    }
}
