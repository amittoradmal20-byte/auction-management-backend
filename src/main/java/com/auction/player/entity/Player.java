package com.auction.player.entity;

import java.time.LocalDate;

import com.auction.entity.BaseEntity;
import com.auction.player.enums.BattingStyle;
import com.auction.player.enums.BowlingStyle;
import com.auction.player.enums.PlayerRole;
import com.auction.player.enums.PlayerStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class Player extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private PlayerRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "batting_style", nullable = false, length = 30)
    private BattingStyle battingStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "bowling_style", nullable = false, length = 40)
    private BowlingStyle bowlingStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PlayerStatus status;

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
}