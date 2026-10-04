package com.auction.tournament.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.auction.tournament.enums.TournamentStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TournamentResponse {

    private UUID id;

    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private TournamentStatus status;

    private Integer maxTeams;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public TournamentStatus getStatus() {
		return status;
	}

	public void setStatus(TournamentStatus status) {
		this.status = status;
	}

	public Integer getMaxTeams() {
		return maxTeams;
	}

	public void setMaxTeams(Integer maxTeams) {
		this.maxTeams = maxTeams;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
}