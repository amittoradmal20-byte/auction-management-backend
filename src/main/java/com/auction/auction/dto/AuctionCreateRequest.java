package com.auction.auction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionCreateRequest {

    @NotNull(message = "Tournament ID is required")
    private UUID tournamentId;

    @NotBlank(message = "Auction name is required")
    @Size(max = 150, message = "Auction name must not exceed 150 characters")
    private String name;

    @Size(max = 500, message = "Auction description must not exceed 500 characters")
    private String description;

    @FutureOrPresent(message = "Start time must be in the present or future")
    private LocalDateTime startTime;

    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    @NotNull(message = "Opening bid is required")
    @DecimalMin(value = "0.01", message = "Opening bid must be greater than zero")
    private BigDecimal openingBid = new BigDecimal("1000.00");

    @NotNull(message = "Bid increment is required")
    @DecimalMin(value = "0.01", message = "Bid increment must be greater than zero")
    private BigDecimal bidIncrement = new BigDecimal("500.00");

	public UUID getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(UUID tournamentId) {
		this.tournamentId = tournamentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getOpeningBid() {
		return openingBid;
	}

	public void setOpeningBid(BigDecimal openingBid) {
		this.openingBid = openingBid;
	}

	public BigDecimal getBidIncrement() {
		return bidIncrement;
	}

	public void setBidIncrement(BigDecimal bidIncrement) {
		this.bidIncrement = bidIncrement;
	}
}