package com.auction.matchschedule.engine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.engine.CalendarFixtureAllocator.AllocatedFixture;
import com.auction.matchschedule.engine.MatchTimeSlotGenerator.MatchTimeSlot;
import com.auction.matchschedule.engine.PlayoffFixtureGenerator.PlayoffMatch;
import com.auction.matchschedule.enums.MatchStage;

@Component
public class PlayoffFixtureAllocator {

	// ==========================================
	// ALLOCATE PLAYOFF FIXTURES
	// ==========================================

	public List<AllocatedPlayoffFixture> allocate(List<PlayoffMatch> playoffMatches,
			List<AllocatedFixture> leagueFixtures, List<MatchTimeSlot> availableSlots,
			SchedulingCalendarConfig config) {

		if (playoffMatches == null) {
			throw new IllegalArgumentException("Playoff matches are required.");
		}

		if (playoffMatches.isEmpty()) {
			return List.of();
		}

		validateInputs(leagueFixtures, availableSlots, config);

		List<AllocatedPlayoffFixture> allocated = new ArrayList<>();

		// ==========================================
		// STEP 1: FIND LEAGUE COMPLETION TIME
		// ==========================================

		LocalDateTime leagueCompletionTime = leagueFixtures.stream()
				.map(fixture -> LocalDateTime.of(fixture.matchDate(), fixture.endTime())).max(Comparator.naturalOrder())
				.orElseThrow();

		LocalDateTime earliestPlayoffStart = leagueCompletionTime.plusMinutes(config.getMinimumRestMinutes());

		// ==========================================
		// STEP 3: GENERATE POSSIBLE PLAYOFF OUTCOMES
		// ==========================================

		List<Map<Integer, PossibleMatchOutcome>> scenarios = new ArrayList<>();

		generateScenarios(playoffMatches, 0, new HashMap<>(), scenarios);

		// ==========================================
		// STEP 4: ALLOCATE PLAYOFF MATCHES
		// ==========================================

		int nextSlotIndex = 0;

		for (PlayoffMatch match : playoffMatches) {

			MatchTimeSlot selectedSlot = null;

			int selectedIndex = -1;

			for (int i = nextSlotIndex; i < availableSlots.size(); i++) {

				MatchTimeSlot candidate = availableSlots.get(i);

				// All league matches must be completed,
				// including the required rest period.

				if (candidate.startDateTime().isBefore(earliestPlayoffStart)) {

					continue;
				}

				// Validate every possible playoff outcome.

				if (!isSlotValidForAllScenarios(match, candidate, allocated, scenarios)) {

					continue;
				}

				selectedSlot = candidate;

				selectedIndex = i;

				break;
			}

			// ==========================================
			// STEP 5: HANDLE INSUFFICIENT SLOTS
			// ==========================================

			if (selectedSlot == null) {

				throw new IllegalStateException(
						"Unable to allocate playoff match " + match.matchNumber() + " (" + match.matchStage().name()
								+ "). " + "Not enough valid calendar slots " + "for the league-to-playoff rest period, "
								+ "match dependencies, ground availability " + "or permitted playing dates and hours.");
			}

			// ==========================================
			// STEP 6: CREATE ALLOCATED PLAYOFF FIXTURE
			// ==========================================

			allocated.add(new AllocatedPlayoffFixture(match.matchNumber(), match.roundNumber(), match.matchStage(),
					match.homeTeamSource(), match.awayTeamSource(), selectedSlot.matchDate(), selectedSlot.startTime(),
					selectedSlot.endTime(), selectedSlot.groundNumber()));

			nextSlotIndex = selectedIndex + 1;
		}

		return List.copyOf(allocated);
	}

	// ==========================================
	// GENERATE ALL POSSIBLE PLAYOFF SCENARIOS
	// ==========================================

	private void generateScenarios(List<PlayoffMatch> matches, int index, Map<Integer, PossibleMatchOutcome> current,
			List<Map<Integer, PossibleMatchOutcome>> scenarios) {

		if (index == matches.size()) {

			scenarios.add(Map.copyOf(current));

			return;
		}

		PlayoffMatch match = matches.get(index);

		int homeRank = resolveTeamSource(match.homeTeamSource(), current);

		int awayRank = resolveTeamSource(match.awayTeamSource(), current);

		if (homeRank == awayRank) {

			throw new IllegalStateException(
					"Invalid playoff fixture. " + "Both team sources resolve to the same rank.");
		}

		// Scenario 1: Home team wins.

		current.put(match.matchNumber(), new PossibleMatchOutcome(homeRank, awayRank, homeRank, awayRank));

		generateScenarios(matches, index + 1, current, scenarios);

		// Scenario 2: Away team wins.

		current.put(match.matchNumber(), new PossibleMatchOutcome(homeRank, awayRank, awayRank, homeRank));

		generateScenarios(matches, index + 1, current, scenarios);

		current.remove(match.matchNumber());
	}

	// ==========================================
	// RESOLVE PLAYOFF TEAM SOURCE
	// ==========================================

	private int resolveTeamSource(String source, Map<Integer, PossibleMatchOutcome> outcomes) {

		if (source == null || source.isBlank()) {

			throw new IllegalArgumentException("Playoff team source is required.");
		}

		if (source.startsWith("RANK_")) {

			int rank = Integer.parseInt(source.substring("RANK_".length()));

			if (rank <= 0) {

				throw new IllegalArgumentException("Invalid playoff rank: " + source);
			}

			return rank;
		}

		if (source.startsWith("WINNER_MATCH_")) {

			int matchNumber = Integer.parseInt(source.substring("WINNER_MATCH_".length()));

			return findOutcome(matchNumber, outcomes).winnerRank();
		}

		if (source.startsWith("LOSER_MATCH_")) {

			int matchNumber = Integer.parseInt(source.substring("LOSER_MATCH_".length()));

			return findOutcome(matchNumber, outcomes).loserRank();
		}

		throw new IllegalArgumentException("Unsupported playoff team source: " + source);
	}

	private PossibleMatchOutcome findOutcome(int matchNumber, Map<Integer, PossibleMatchOutcome> outcomes) {

		PossibleMatchOutcome outcome = outcomes.get(matchNumber);

		if (outcome == null) {

			throw new IllegalStateException("Invalid playoff match dependency: " + matchNumber);
		}

		return outcome;
	}

	// ==========================================
	// VALIDATE SLOT FOR ALL POSSIBLE SCENARIOS
	// ==========================================

	private boolean isSlotValidForAllScenarios(PlayoffMatch match, MatchTimeSlot candidate,
			List<AllocatedPlayoffFixture> allocated, List<Map<Integer, PossibleMatchOutcome>> scenarios) {

		for (Map<Integer, PossibleMatchOutcome> scenario : scenarios) {

			PossibleMatchOutcome outcome = findOutcome(match.matchNumber(), scenario);

			if (!isRankAvailable(outcome.homeRank(), candidate, allocated, scenario)) {

				return false;
			}

			if (!isRankAvailable(outcome.awayRank(), candidate, allocated, scenario)) {

				return false;
			}
		}

		return true;
	}

//==========================================
//VALIDATE POSSIBLE TEAM AVAILABILITY
//==========================================

	private boolean isRankAvailable(int rank, MatchTimeSlot candidate, List<AllocatedPlayoffFixture> allocated,
			Map<Integer, PossibleMatchOutcome> scenario) {

		for (AllocatedPlayoffFixture previous : allocated) {

			PossibleMatchOutcome previousOutcome = findOutcome(previous.matchNumber(), scenario);

			boolean participated = previousOutcome.homeRank() == rank || previousOutcome.awayRank() == rank;

			if (!participated) {

				continue;
			}

			// ==========================================
			// VALIDATE PLAYOFF MATCH COMPLETION
			// ==========================================

			// A team cannot participate in overlapping
			// playoff matches.
			//
			// The previous playoff match must be completed
			// before the next playoff match starts.

			if (candidate.startDateTime().isBefore(previous.endDateTime())) {

				return false;
			}
		}

		// IMPORTANT:
		//
		// The following league fairness restrictions
		// are intentionally NOT applied to playoffs:
		//
		// 1. Minimum rest between playoff matches.
		// 2. Minimum matches between appearances.
		// 3. Maximum matches per team per day.
		//
		// Match overlap prevention remains mandatory.

		return true;
	}

	// ==========================================
	// VALIDATE INPUTS
	// ==========================================

	private void validateInputs(List<AllocatedFixture> leagueFixtures, List<MatchTimeSlot> availableSlots,
			SchedulingCalendarConfig config) {

		if (leagueFixtures == null || leagueFixtures.isEmpty()) {

			throw new IllegalArgumentException("League fixtures are required " + "before playoff allocation.");
		}

		if (availableSlots == null || availableSlots.isEmpty()) {

			throw new IllegalArgumentException("No available playoff time slots.");
		}

		if (config == null) {

			throw new IllegalArgumentException("Scheduling configuration is required.");
		}

		if (config.getMinimumRestMinutes() == null || config.getMinimumRestMinutes() < 0) {

			throw new IllegalArgumentException("Minimum rest minutes cannot be negative.");
		}

	}

	// ==========================================
	// POSSIBLE PLAYOFF MATCH OUTCOME
	// ==========================================

	private record PossibleMatchOutcome(int homeRank, int awayRank, int winnerRank, int loserRank) {
	}

	// ==========================================
	// ALLOCATED PLAYOFF FIXTURE RECORD
	// ==========================================

	public record AllocatedPlayoffFixture(

			int matchNumber,

			int roundNumber,

			MatchStage matchStage,

			String homeTeamSource,

			String awayTeamSource,

			LocalDate matchDate,

			LocalTime startTime,

			LocalTime endTime,

			int groundNumber

	) {

		public LocalDateTime startDateTime() {

			return LocalDateTime.of(matchDate, startTime);
		}

		public LocalDateTime endDateTime() {

			return LocalDateTime.of(matchDate, endTime);
		}
	}
}