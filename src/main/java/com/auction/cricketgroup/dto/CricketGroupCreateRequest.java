package com.auction.cricketgroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CricketGroupCreateRequest {

    @NotBlank(message = "Cricket group name is required")
    @Size(
        max = 150,
        message = "Cricket group name must not exceed 150 characters"
    )
    private String name;

    @Size(
        max = 500,
        message = "Description must not exceed 500 characters"
    )
    private String description;

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
}
