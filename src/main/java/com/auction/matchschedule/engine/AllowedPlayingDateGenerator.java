
package com.auction.matchschedule.engine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.enums.SchedulingType;

@Component
public class AllowedPlayingDateGenerator {

    public List<LocalDate> generate(
            SchedulingCalendarConfig config) {

        validateConfiguration(config);

        Set<LocalDate> allowedDates = new HashSet<>();

        SchedulingType type = config.getSchedulingType();

        switch (type) {

            case DAILY:
                generateDailyDates(config, allowedDates);
                break;

            case WEEKLY:
                generateWeeklyDates(config, allowedDates);
                break;

            case WEEKEND:
                generateWeekendDates(config, allowedDates);
                break;

            case TWO_DAY:
                generateSelectedDates(config, allowedDates, 2);
                break;

            case THREE_DAY:
                generateSelectedDates(config, allowedDates, 3);
                break;

            case CUSTOM:
                generateSelectedDates(config, allowedDates, -1);
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported scheduling type: " + type
                );
        }

        allowedDates.removeAll(
                config.getExcludedDates() == null
                        ? Set.of()
                        : config.getExcludedDates()
        );

        if (allowedDates.isEmpty()) {
            throw new IllegalStateException(
                    "No available playing dates remain after "
                    + "applying the scheduling rules and exclusions."
            );
        }

        return allowedDates.stream()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    private void generateDailyDates(
            SchedulingCalendarConfig config,
            Set<LocalDate> dates) {

        LocalDate current = config.getStartDate();

        while (!current.isAfter(config.getEndDate())) {
            dates.add(current);
            current = current.plusDays(1);
        }
    }

    private void generateWeeklyDates(
            SchedulingCalendarConfig config,
            Set<LocalDate> dates) {

        Set<DayOfWeek> weekdays =
                config.getAllowedDaysOfWeek();

        if (weekdays == null || weekdays.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one weekday must be selected "
                    + "for WEEKLY scheduling."
            );
        }

        LocalDate current = config.getStartDate();

        while (!current.isAfter(config.getEndDate())) {

            if (weekdays.contains(current.getDayOfWeek())) {
                dates.add(current);
            }

            current = current.plusDays(1);
        }
    }

    private void generateWeekendDates(
            SchedulingCalendarConfig config,
            Set<LocalDate> dates) {

        Set<DayOfWeek> selectedDays =
                config.getAllowedDaysOfWeek();

        if (selectedDays == null || selectedDays.isEmpty()) {
            throw new IllegalArgumentException(
                    "Select Saturday, Sunday, or both "
                    + "for WEEKEND scheduling."
            );
        }

        for (DayOfWeek day : selectedDays) {
            if (day != DayOfWeek.SATURDAY
                    && day != DayOfWeek.SUNDAY) {

                throw new IllegalArgumentException(
                        "WEEKEND scheduling only supports "
                        + "Saturday and Sunday."
                );
            }
        }

        LocalDate current = config.getStartDate();

        while (!current.isAfter(config.getEndDate())) {

            if (selectedDays.contains(current.getDayOfWeek())) {
                dates.add(current);
            }

            current = current.plusDays(1);
        }
    }

    private void generateSelectedDates(
            SchedulingCalendarConfig config,
            Set<LocalDate> dates,
            int requiredCount) {

        Set<LocalDate> selectedDates =
                config.getSelectedDates();

        if (selectedDates == null || selectedDates.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one playing date must be selected."
            );
        }

        if (selectedDates.contains(null)) {
            throw new IllegalArgumentException(
                    "Selected dates cannot contain null."
            );
        }

        if (requiredCount > 0
                && selectedDates.size() != requiredCount) {

            throw new IllegalArgumentException(
                    "Exactly " + requiredCount
                    + " playing dates are required."
            );
        }

        for (LocalDate date : selectedDates) {

            if (date.isBefore(config.getStartDate())
                    || date.isAfter(config.getEndDate())) {

                throw new IllegalArgumentException(
                        "Selected date " + date
                        + " is outside the tournament date range."
                );
            }

            dates.add(date);
        }
    }

    private void validateConfiguration(
            SchedulingCalendarConfig config) {

        if (config == null) {
            throw new IllegalArgumentException(
                    "Scheduling configuration is required."
            );
        }

        if (config.getSchedulingType() == null) {
            throw new IllegalArgumentException(
                    "Scheduling type is required."
            );
        }

        if (config.getStartDate() == null
                || config.getEndDate() == null) {

            throw new IllegalArgumentException(
                    "Start date and end date are required."
            );
        }

        if (config.getEndDate().isBefore(
                config.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }

        if (config.getExcludedDates() != null
                && config.getExcludedDates().contains(null)) {

            throw new IllegalArgumentException(
                    "Excluded dates cannot contain null."
            );
        }
    }

}
