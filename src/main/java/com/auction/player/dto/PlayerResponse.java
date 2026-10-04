package com.auction.player.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.auction.player.enums.BattingStyle;
import com.auction.player.enums.BowlingStyle;
import com.auction.player.enums.PlayerRole;
import com.auction.player.enums.PlayerStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String displayName;

    private LocalDate dateOfBirth;

    private String nationality;

    private String profileImageUrl;

    private PlayerRole role;

    private BattingStyle battingStyle;

    private BowlingStyle bowlingStyle;

    private PlayerStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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

	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
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