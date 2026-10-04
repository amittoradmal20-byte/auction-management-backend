package com.auction.player.dto;

import java.time.LocalDate;

import com.auction.player.enums.BattingStyle;
import com.auction.player.enums.BowlingStyle;
import com.auction.player.enums.PlayerRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerUpdateRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Display name is required")
    @Size(max = 150, message = "Display name must not exceed 150 characters")
    private String displayName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;

    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImageUrl;

    @NotNull(message = "Player role is required")
    private PlayerRole role;

    @NotNull(message = "Batting style is required")
    private BattingStyle battingStyle;

    @NotNull(message = "Bowling style is required")
    private BowlingStyle bowlingStyle;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public PlayerRole getRole() {
		return role;
	}

	public void setRole(PlayerRole role) {
		this.role = role;
	}

	public BattingStyle getBattingStyle() {
		return battingStyle;
	}

	public void setBattingStyle(BattingStyle battingStyle) {
		this.battingStyle = battingStyle;
	}

	public BowlingStyle getBowlingStyle() {
		return bowlingStyle;
	}

	public void setBowlingStyle(BowlingStyle bowlingStyle) {
		this.bowlingStyle = bowlingStyle;
	}
}