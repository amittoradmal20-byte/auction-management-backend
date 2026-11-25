
package com.auction.matchschedule.engine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MatchTimeSlotGenerator {

	/**
	 * Generates available time slots for the selected playing dates.
	 *
	 * This method does not assign teams or fixtures.
	 */
	public List<MatchTimeSlot> generate(List<LocalDate> playingDates, SchedulingCalendarConfig config) {

		validateConfiguration(playingDates, config);

		List<MatchTimeSlot> slots = new ArrayList<>();

		for (LocalDate playingDate : playingDates) {

			for (int groundNumber = 1; groundNumber <= config.getNumberOfGrounds(); groundNumber++) {

				LocalTime currentStart = config.getDailyStartTime();

				while (true) {

					long remainingMinutes = java.time.Duration.between(currentStart, config.getDailyEndTime())
							.toMinutes();

					// Stop when the remaining daily window cannot
					// accommodate another complete match.
					if (remainingMinutes < config.getMatchDurationMinutes()) {
						break;
					}

					LocalTime currentEnd = currentStart.plusMinutes(config.getMatchDurationMinutes());

					slots.add(new MatchTimeSlot(playingDate, currentStart, currentEnd, groundNumber));

					long stepMinutes = (long) config.getMatchDurationMinutes() + config.getBreakBetweenMatchesMinutes();

					// Prevent LocalTime from wrapping around midnight.
					// The next match must start within the same playing day.
					if (stepMinutes >= remainingMinutes) {
						break;
					}

					currentStart = currentStart.plusMinutes(stepMinutes);
				}

			}
		}

		// Chronological ordering across all grounds.
		slots.sort(Comparator.comparing(MatchTimeSlot::matchDate).thenComparing(MatchTimeSlot::startTime)
				.thenComparingInt(MatchTimeSlot::groundNumber));

		return List.copyOf(slots);
	}

	private void validateConfiguration(List<LocalDate> playingDates, SchedulingCalendarConfig config) {

		if (playingDates == null || playingDates.isEmpty()) {
			throw new IllegalArgumentException("At least one playing date is required.");
		}

		if (playingDates.stream().anyMatch(date -> date == null)) {
			throw new IllegalArgumentException("Playing dates cannot contain null.");
		}

		if (config == null) {
			throw new IllegalArgumentException("Scheduling configuration is required.");
		}

		if (config.getDailyStartTime() == null || config.getDailyEndTime() == null) {

			throw new IllegalArgumentException("Daily start and end times are required.");
		}

		if (!config.getDailyEndTime().isAfter(config.getDailyStartTime())) {

			throw new IllegalArgumentException("Daily end time must be after start time.");
		}

		if (config.getMatchDurationMinutes() == null || config.getMatchDurationMinutes() <= 0) {

			throw new IllegalArgumentException("Match duration must be greater than zero.");
		}

		if (config.getBreakBetweenMatchesMinutes() == null || config.getBreakBetweenMatchesMinutes() < 0) {

			throw new IllegalArgumentException("Break between matches cannot be negative.");
		}

		if (config.getNumberOfGrounds() == null || config.getNumberOfGrounds() <= 0) {

			throw new IllegalArgumentException("Number of grounds must be greater than zero.");
		}

		long availableMinutes = java.time.Duration.between(config.getDailyStartTime(), config.getDailyEndTime())
				.toMinutes();

		if (config.getMatchDurationMinutes() > availableMinutes) {
			throw new IllegalArgumentException("Match duration exceeds available daily playing hours.");
		}
	}

	/**
	 * Represents one available ground/time slot.
	 */
	public record MatchTimeSlot(LocalDate matchDate, LocalTime startTime, LocalTime endTime, int groundNumber) {

		public LocalDateTime startDateTime() {
			return LocalDateTime.of(matchDate, startTime);
		}

		public LocalDateTime endDateTime() {
			return LocalDateTime.of(matchDate, endTime);
		}
	}

}
