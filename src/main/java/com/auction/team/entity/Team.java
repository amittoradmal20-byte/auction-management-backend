package com.auction.team.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.auction.entity.BaseEntity;
import com.auction.team.enums.TeamStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "teams",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_teams_name",
            columnNames = {"name"}
        ),
        @UniqueConstraint(
            name = "uk_teams_short_name",
            columnNames = {"short_name"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Team extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "short_name", nullable = false, length = 20)
    private String shortName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TeamStatus status;

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

	public TeamStatus getStatus() {
		return status;
	}

	public void setStatus(TeamStatus status) {
		this.status = status;
	}
}
