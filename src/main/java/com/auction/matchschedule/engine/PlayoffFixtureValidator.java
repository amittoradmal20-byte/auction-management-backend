package com.auction.matchschedule.engine;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.auction.matchschedule.engine.CalendarFixtureAllocator.AllocatedFixture;
import com.auction.matchschedule.engine.PlayoffFixtureAllocator.AllocatedPlayoffFixture;
import com.auction.matchschedule.engine.PlayoffFixtureGenerator.PlayoffMatch;
import com.auction.matchschedule.enums.PlayoffFormat;
@Component
public class PlayoffFixtureValidator {
	private final AllowedPlayingDateGenerator allowedPlayingDateGenerator;
	private final PlayoffFixtureGenerator playoffFixtureGenerator;
	public PlayoffFixtureValidator(AllowedPlayingDateGenerator allowedPlayingDateGenerator,
			PlayoffFixtureGenerator playoffFixtureGenerator) {
		this.allowedPlayingDateGenerator = allowedPlayingDateGenerator;
		this.playoffFixtureGenerator = playoffFixtureGenerator;
	}
	// ==========================================
	// VALIDATE PLAYOFF FIXTURES
	// ==========================================
	public void validate(PlayoffFormat playoffFormat, List<AllocatedFixture> leagueFixtures,
			List<AllocatedPlayoffFixture> playoffFixtures, SchedulingCalendarConfig config) {
		if (playoffFormat == null || playoffFixtures == null || config == null) {
			throw new IllegalArgumentException("Playoff validation inputs are required.");
		}
		// ==========================================
		// STEP 1: VALIDATE PLAYOFF MATCH COUNT
		// ==========================================
		if (playoffFixtures.size() != playoffFormat.getPlayoffMatchCount()) {
			throw new IllegalStateException("Incorrect playoff match count. Expected: "
					+ playoffFormat.getPlayoffMatchCount() + ", Actual: " + playoffFixtures.size());
		}
		if (!playoffFormat.hasPlayoffs()) {
			return;
		}
		if (leagueFixtures == null || leagueFixtures.isEmpty()) {
			throw new IllegalArgumentException("League fixtures are required " + "for playoff validation.");
		}
		validateConfiguration(config);
		// ==========================================
		// STEP 2: VALIDATE QUALIFYING TEAM COUNT
		// ==========================================
		Set<UUID> leagueTeamIds = new HashSet<>();
		for (AllocatedFixture fixture : leagueFixtures) {
			leagueTeamIds.add(fixture.homeTournamentTeamId());
			leagueTeamIds.add(fixture.awayTournamentTeamId());
		}
		playoffFormat.validateTeamCount(leagueTeamIds.size());
		// ==========================================
		// STEP 3: FIND LEAGUE COMPLETION DETAILS
		// ==========================================
		int lastLeagueMatchNumber = leagueFixtures.stream().mapToInt(AllocatedFixture::matchNumber).max().orElseThrow();
		int lastLeagueRoundNumber = leagueFixtures.stream().mapToInt(AllocatedFixture::roundNumber).max().orElseThrow();
		LocalDateTime leagueCompletionTime = leagueFixtures.stream()
				.map(fixture -> LocalDateTime.of(fixture.matchDate(), fixture.endTime())).max(Comparator.naturalOrder())
				.orElseThrow();
		// ==========================================
		// STEP 4: GENERATE EXPECTED PLAYOFF STRUCTURE
		// ==========================================
		List<PlayoffMatch> expectedMatches = playoffFixtureGenerator.generate(playoffFormat, lastLeagueMatchNumber,
				lastLeagueRoundNumber);
		Set<LocalDate> allowedDates = new HashSet<>(allowedPlayingDateGenerator.generate(config));
		Map<Integer, AllocatedPlayoffFixture> fixtureByNumber = new HashMap<>();
		// ==========================================
		// STEP 5: VALIDATE INDIVIDUAL FIXTURES
		// ==========================================
		for (AllocatedPlayoffFixture fixture : playoffFixtures) {
			validateFixtureFields(fixture, config, allowedDates);
			if (fixtureByNumber.putIfAbsent(fixture.matchNumber(), fixture) != null) {
				throw new IllegalStateException("Duplicate playoff match number: " + fixture.matchNumber());
			}
			if (fixture.startDateTime().isBefore(leagueCompletionTime)) {
				throw new IllegalStateException(
						"Playoff match " + fixture.matchNumber() + " starts before league completion.");
			}
		}
		// ==========================================
		// STEP 6: VALIDATE PLAYOFF STRUCTURE
		// ==========================================
		List<AllocatedPlayoffFixture> orderedFixtures = new ArrayList<>();
		for (PlayoffMatch expected : expectedMatches) {
			AllocatedPlayoffFixture actual = fixtureByNumber.get(expected.matchNumber());
			if (actual == null) {
				throw new IllegalStateException("Missing playoff match: " + expected.matchNumber());
			}
			validatePlayoffStructure(expected, actual);
			orderedFixtures.add(actual);
		}
		// ==========================================
		// STEP 7: VALIDATE MATCH DEPENDENCIES
		// ==========================================
		for (AllocatedPlayoffFixture fixture : orderedFixtures) {
			validateDependency(fixture.homeTeamSource(), fixture, fixtureByNumber);
			validateDependency(fixture.awayTeamSource(), fixture, fixtureByNumber);
		}
		// ==========================================
		// STEP 8: VALIDATE GROUND AVAILABILITY
		// ==========================================
		validateGroundAvailability(leagueFixtures, orderedFixtures, config);
		// ==========================================
		// STEP 9: VALIDATE POSSIBLE TEAM APPEARANCES
		// ==========================================
		validatePossibleOutcomes(orderedFixtures, 0, new HashMap<>(), leagueCompletionTime, config);
	}
	// ==========================================
	// VALIDATE CONFIGURATION
	// ==========================================
	private void validateConfiguration(SchedulingCalendarConfig config) {
		if (config.getMinimumRestMinutes() == null || config.getMinimumRestMinutes() < 0) {
			throw new IllegalArgumentException("Minimum rest minutes must be zero or greater.");
		}
		if (config.getBreakBetweenMatchesMinutes() == null || config.getBreakBetweenMatchesMinutes() < 0) {
			throw new IllegalArgumentException("Break between matches must be zero or greater.");
		}

		if (config.getNumberOfGrounds() == null || config.getNumberOfGrounds() <= 0) {
			throw new IllegalArgumentException("Number of grounds must be positive.");
		}
		if (config.getMatchDurationMinutes() == null || config.getMatchDurationMinutes() <= 0) {
			throw new IllegalArgumentException("Match duration must be positive.");
		}
		if (config.getDailyStartTime() == null || config.getDailyEndTime() == null
				|| !config.getDailyEndTime().isAfter(config.getDailyStartTime())) {
			throw new IllegalArgumentException("Invalid daily playing hours.");
		}
		// Minimum matches between appearances
		// is intentionally not validated for playoffs.
	}
	// ==========================================
	// VALIDATE FIXTURE FIELDS
	// ==========================================
	private void validateFixtureFields(AllocatedPlayoffFixture fixture, SchedulingCalendarConfig config,
			Set<LocalDate> allowedDates) {
		if (fixture == null || fixture.matchStage() == null || fixture.homeTeamSource() == null
				|| fixture.awayTeamSource() == null || fixture.matchDate() == null || fixture.startTime() == null
				|| fixture.endTime() == null) {
			throw new IllegalArgumentException("Playoff fixture contains missing fields.");
		}
		if (fixture.matchNumber() <= 0 || fixture.roundNumber() <= 0) {
			throw new IllegalStateException("Match and round numbers must be positive.");
		}
		if (!fixture.matchStage().isPlayoff()) {
			throw new IllegalStateException("Invalid playoff match stage: " + fixture.matchStage());
		}
		if (fixture.homeTeamSource().isBlank() || fixture.awayTeamSource().isBlank()
				|| fixture.homeTeamSource().equals(fixture.awayTeamSource())) {
			throw new IllegalStateException("Invalid playoff team sources for match " + fixture.matchNumber());
		}
		if (!allowedDates.contains(fixture.matchDate())) {
			throw new IllegalStateException(
					"Playoff match " + fixture.matchNumber() + " is scheduled on a disallowed date.");
		}
		if (fixture.groundNumber() < 1 || fixture.groundNumber() > config.getNumberOfGrounds()) {
			throw new IllegalStateException("Invalid ground number for playoff match " + fixture.matchNumber());
		}
		if (!fixture.endTime().isAfter(fixture.startTime())) {
			throw new IllegalStateException("Invalid playoff match timing.");
		}
		if (fixture.startTime().isBefore(config.getDailyStartTime())
				|| fixture.endTime().isAfter(config.getDailyEndTime())) {
			throw new IllegalStateException(
					"Playoff match " + fixture.matchNumber() + " is outside permitted playing hours.");
		}
		Duration actualDuration = Duration.between(fixture.startTime(), fixture.endTime());
		Duration expectedDuration = Duration.ofMinutes(config.getMatchDurationMinutes());
		if (!actualDuration.equals(expectedDuration)) {
			throw new IllegalStateException("Incorrect duration for playoff match " + fixture.matchNumber());
		}
	}
	// ==========================================
	// VALIDATE EXPECTED PLAYOFF STRUCTURE
	// ==========================================
	private void validatePlayoffStructure(PlayoffMatch expected, AllocatedPlayoffFixture actual) {
		if (expected.matchNumber() != actual.matchNumber() || expected.roundNumber() != actual.roundNumber()
				|| expected.matchStage() != actual.matchStage()
				|| !Objects.equals(expected.homeTeamSource(), actual.homeTeamSource())
				|| !Objects.equals(expected.awayTeamSource(), actual.awayTeamSource())) {
			throw new IllegalStateException("Incorrect playoff structure for match " + actual.matchNumber());
		}
	}
	// ==========================================
	// VALIDATE MATCH DEPENDENCY
	// ==========================================
	private void validateDependency(String source, AllocatedPlayoffFixture current,
			Map<Integer, AllocatedPlayoffFixture> fixtures) {
		if (source.startsWith("RANK_")) {
			return;
		}
		String prefix;
		if (source.startsWith("WINNER_MATCH_")) {
			prefix = "WINNER_MATCH_";
		} else if (source.startsWith("LOSER_MATCH_")) {
			prefix = "LOSER_MATCH_";
		} else {
			throw new IllegalStateException("Unsupported playoff source: " + source);
		}
		int dependencyNumber = Integer.parseInt(source.substring(prefix.length()));
		AllocatedPlayoffFixture dependency = fixtures.get(dependencyNumber);
		if (dependency == null || dependencyNumber >= current.matchNumber()) {
			throw new IllegalStateException("Invalid dependency for playoff match " + current.matchNumber());
		}
		if (current.startDateTime().isBefore(dependency.endDateTime())) {
			throw new IllegalStateException("Playoff match " + current.matchNumber() + " starts before dependent match "
					+ dependencyNumber + " is completed.");
		}
	}
	// ==========================================
	// VALIDATE GROUND AVAILABILITY
	// ==========================================
	private void validateGroundAvailability(List<AllocatedFixture> leagueFixtures,
			List<AllocatedPlayoffFixture> playoffFixtures, SchedulingCalendarConfig config) {
		List<GroundBooking> bookings = new ArrayList<>();
		for (AllocatedFixture fixture : leagueFixtures) {
			bookings.add(new GroundBooking(fixture.matchNumber(), fixture.groundNumber(),
					LocalDateTime.of(fixture.matchDate(), fixture.startTime()),
					LocalDateTime.of(fixture.matchDate(), fixture.endTime())));
		}
		for (AllocatedPlayoffFixture fixture : playoffFixtures) {
			bookings.add(new GroundBooking(fixture.matchNumber(), fixture.groundNumber(), fixture.startDateTime(),
					fixture.endDateTime()));
		}
		bookings.sort(Comparator.comparing(GroundBooking::start).thenComparingInt(GroundBooking::groundNumber)
				.thenComparingInt(GroundBooking::matchNumber));
		Map<Integer, LocalDateTime> lastGroundEnd = new HashMap<>();
		for (GroundBooking booking : bookings) {
			LocalDateTime previousEnd = lastGroundEnd.get(booking.groundNumber());
			if (previousEnd != null
					&& booking.start().isBefore(previousEnd.plusMinutes(config.getBreakBetweenMatchesMinutes()))) {
				throw new IllegalStateException("Ground " + booking.groundNumber() + " has overlapping matches or "
						+ "insufficient break before match " + booking.matchNumber());
			}
			lastGroundEnd.put(booking.groundNumber(), booking.end());
		}
	}
	// ==========================================
	// VALIDATE ALL POSSIBLE PLAYOFF OUTCOMES
	// ==========================================
	private void validatePossibleOutcomes(List<AllocatedPlayoffFixture> fixtures, int index,
			Map<Integer, MatchOutcome> outcomes, LocalDateTime leagueCompletionTime, SchedulingCalendarConfig config) {
		if (index == fixtures.size()) {
			validateScenario(fixtures, outcomes, leagueCompletionTime, config);
			return;
		}
		AllocatedPlayoffFixture fixture = fixtures.get(index);
		int homeRank = resolveRank(fixture.homeTeamSource(), outcomes);
		int awayRank = resolveRank(fixture.awayTeamSource(), outcomes);
		if (homeRank == awayRank) {
			throw new IllegalStateException("Playoff match " + fixture.matchNumber() + " resolves to the same team.");
		}
		// Scenario 1: Home team wins.
		outcomes.put(fixture.matchNumber(), new MatchOutcome(homeRank, awayRank, homeRank, awayRank));
		validatePossibleOutcomes(fixtures, index + 1, outcomes, leagueCompletionTime, config);
		// Scenario 2: Away team wins.
		outcomes.put(fixture.matchNumber(), new MatchOutcome(homeRank, awayRank, awayRank, homeRank));
		validatePossibleOutcomes(fixtures, index + 1, outcomes, leagueCompletionTime, config);
		outcomes.remove(fixture.matchNumber());
	}
	// ==========================================
	// RESOLVE TEAM RANK
	// ==========================================
	private int resolveRank(String source, Map<Integer, MatchOutcome> outcomes) {
		if (source.startsWith("RANK_")) {
			int rank = Integer.parseInt(source.substring("RANK_".length()));
			if (rank <= 0) {
				throw new IllegalStateException("Invalid qualifying rank: " + source);
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
		throw new IllegalStateException("Invalid playoff team source: " + source);
	}
	private MatchOutcome findOutcome(int matchNumber, Map<Integer, MatchOutcome> outcomes) {
		MatchOutcome outcome = outcomes.get(matchNumber);
		if (outcome == null) {
			throw new IllegalStateException("Missing playoff outcome for match " + matchNumber);
		}
		return outcome;
	}
	// ==========================================
	// VALIDATE INDIVIDUAL OUTCOME SCENARIO
	// ==========================================
	private void validateScenario(List<AllocatedPlayoffFixture> fixtures, Map<Integer, MatchOutcome> outcomes,
			LocalDateTime leagueCompletionTime,
			SchedulingCalendarConfig config) {
		List<AllocatedPlayoffFixture> chronological = new ArrayList<>(fixtures);
		chronological.sort(Comparator.comparing(AllocatedPlayoffFixture::startDateTime)
				.thenComparingInt(AllocatedPlayoffFixture::groundNumber)
				.thenComparingInt(AllocatedPlayoffFixture::matchNumber));
		Map<Integer, LocalDateTime> lastRankEnd = new HashMap<>();
		for (AllocatedPlayoffFixture fixture : chronological) {
			MatchOutcome outcome = findOutcome(fixture.matchNumber(), outcomes);
			validateRankAppearance(outcome.homeRank(), fixture, lastRankEnd, leagueCompletionTime, config);
			validateRankAppearance(outcome.awayRank(), fixture, lastRankEnd, leagueCompletionTime, config);
		}
	}
	// ==========================================
	// VALIDATE POSSIBLE TEAM APPEARANCE
	// ==========================================
	private void validateRankAppearance(int rank, AllocatedPlayoffFixture fixture,
			Map<Integer, LocalDateTime> lastRankEnd,
			LocalDateTime leagueCompletionTime,
			SchedulingCalendarConfig config) {
		// ==========================================
		// STEP 1: DETERMINE EARLIEST ALLOWED START
		// ==========================================
		LocalDateTime previousPlayoffEnd = lastRankEnd.get(rank);
		LocalDateTime earliestStart;
		if (previousPlayoffEnd == null) {
			// First playoff appearance.
			//
			// The configured minimum rest period
			// remains applicable after league completion.
			earliestStart = leagueCompletionTime.plusMinutes(config.getMinimumRestMinutes());
		} else {
			// Subsequent playoff appearance.
			//
			// Minimum rest minutes are intentionally
			// NOT applied between playoff matches.
			//
			// The previous playoff match must still
			// be completed before the next match starts.
			earliestStart = previousPlayoffEnd;
		}
		// ==========================================
		// STEP 2: VALIDATE MATCH START TIME
		// ==========================================
		if (fixture.startDateTime().isBefore(earliestStart)) {
			if (previousPlayoffEnd == null) {
				throw new IllegalStateException("Possible qualifying team RANK_" + rank
						+ " has insufficient rest after league " + "completion before match " + fixture.matchNumber());
			}
			throw new IllegalStateException("Possible qualifying team RANK_" + rank
					+ " has overlapping playoff matches " + "before match " + fixture.matchNumber());
		}
		// ==========================================
		// STEP 3: UPDATE LAST PLAYOFF END TIME
		// ==========================================
		lastRankEnd.put(rank, fixture.endDateTime());
		// IMPORTANT:
		//
		// Minimum rest minutes apply only between
		// league completion and the first playoff appearance.
		//
		// Minimum rest minutes do NOT apply between
		// consecutive playoff matches.
		//
		// Minimum matches between appearances
		// does not apply to playoff matches.
		//
		// Maximum daily match limits apply only to league matches.
		// Match overlap prevention remains applicable.
	}
	// ==========================================
	// INTERNAL RECORDS
	// ==========================================
	private record GroundBooking(int matchNumber, int groundNumber, LocalDateTime start, LocalDateTime end) {
	}
	private record MatchOutcome(int homeRank, int awayRank, int winnerRank, int loserRank) {
	}
}
