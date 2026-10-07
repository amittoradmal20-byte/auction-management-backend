package com.auction.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeamCreateRequest {

    @NotBlank(message = "Team name is required")
    @Size(
        max = 100,
        message = "Team name must not exceed 100 characters"
    )
    private String name;

    @NotBlank(message = "Short name is required")
    @Size(
        max = 20,
        message = "Team short name must not exceed 20 characters"
    )
    private String shortName;

    @Size(
        max = 500,
        message = "Logo URL must not exceed 500 characters"
    )
    private String logoUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
}
