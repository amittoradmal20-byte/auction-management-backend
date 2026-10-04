package com.auction.tournament.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TournamentUpdateRequest {

    @Size(max = 150, message = "Tournament name must not exceed 150 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Min(value = 2, message = "Tournament must have at least 2 teams")
    private Integer maxTeams;

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

	public Integer getMaxTeams() {
		return maxTeams;
	}

	public void setMaxTeams(Integer maxTeams) {
		this.maxTeams = maxTeams;
	}
    
}