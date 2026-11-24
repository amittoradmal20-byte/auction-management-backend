
package com.auction.matchschedule.entity;

import java.time.LocalDate;

import com.auction.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "match_schedule_excluded_dates",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_match_excluded_date",
            columnNames = {
                "match_schedule_id",
                "excluded_date"
            }
        )
    }
)
public class MatchScheduleExcludedDate extends BaseEntity {

    // ==========================================
    // MATCH SCHEDULE
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "match_schedule_id",
        nullable = false
    )
    private MatchSchedule matchSchedule;

    // ==========================================
    // EXCLUDED DATE
    // ==========================================

    @Column(
        name = "excluded_date",
        nullable = false
    )
    private LocalDate excludedDate;

    // ==========================================
    // REASON
    // ==========================================

    @Column(
        name = "reason",
        length = 250
    )
    private String reason;

	public MatchSchedule getMatchSchedule() {
		return matchSchedule;
	}

	public void setMatchSchedule(MatchSchedule matchSchedule) {
		this.matchSchedule = matchSchedule;
	}

	public LocalDate getExcludedDate() {
		return excludedDate;
	}

	public void setExcludedDate(LocalDate excludedDate) {
		this.excludedDate = excludedDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
    
}
