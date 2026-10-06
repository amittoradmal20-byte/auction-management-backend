package com.auction.cricketgroup.entity;

import com.auction.cricketgroup.enums.CricketGroupStatus;
import com.auction.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cricket_groups")
@Getter
@Setter
@NoArgsConstructor
public class CricketGroup extends BaseEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CricketGroupStatus status;

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

	public CricketGroupStatus getStatus() {
		return status;
	}

	public void setStatus(CricketGroupStatus status) {
		this.status = status;
	}
}
