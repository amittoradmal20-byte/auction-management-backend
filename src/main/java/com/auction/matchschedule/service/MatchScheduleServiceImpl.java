package com.auction.matchschedule.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.matchschedule.dto.request.MatchScheduleCreateRequest;
import com.auction.matchschedule.dto.request.MatchScheduleGenerateRequest;
import com.auction.matchschedule.dto.response.MatchScheduleFixtureResponse;
import com.auction.matchschedule.dto.response.MatchScheduleGenerationResponse;
import com.auction.matchschedule.dto.response.MatchScheduleResponse;
import com.auction.matchschedule.dto.response.MatchScheduleTeamDrawResponse;

import com.auction.matchschedule.engine.AllocatedFixtureValidator;
import com.auction.matchschedule.engine.AllowedPlayingDateGenerator;
import com.auction.matchschedule.engine.CalendarFixtureAllocator;
import com.auction.matchschedule.engine.FairMatchOrderingEngine;
import com.auction.matchschedule.engine.MatchTimeSlotGenerator;
import com.auction.matchschedule.engine.PartialRoundRobinGenerator;
import com.auction.matchschedule.engine.RandomTeamDrawGenerator;
import com.auction.matchschedule.engine.RoundRobinGenerator;
import com.auction.matchschedule.engine.SchedulingCalendarConfig;

import com.auction.matchschedule.engine.CalendarFixtureAllocator.AllocatedFixture;
import com.auction.matchschedule.engine.MatchTimeSlotGenerator.MatchTimeSlot;
import com.auction.matchschedule.engine.RoundRobinGenerator.RoundRobinMatch;

import com.auction.matchschedule.entity.MatchSchedule;
import com.auction.matchschedule.entity.MatchScheduleExcludedDate;
import com.auction.matchschedule.entity.MatchScheduleFixture;
import com.auction.matchschedule.entity.MatchScheduleTeamDraw;

import com.auction.matchschedule.enums.MatchScheduleStatus;
import com.auction.matchschedule.enums.MatchStage;
import com.auction.matchschedule.enums.SchedulingType;

import com.auction.matchschedule.mapper.MatchScheduleMapper;

import com.auction.matchschedule.repository.MatchScheduleExcludedDateRepository;
import com.auction.matchschedule.repository.MatchScheduleFixtureRepository;
import com.auction.matchschedule.repository.MatchScheduleRepository;
import com.auction.matchschedule.repository.MatchScheduleTeamDrawRepository;

import com.auction.matchschedule.service.MatchScheduleService;

import com.auction.tournament.entity.Tournament;
import com.auction.tournament.repository.TournamentRepository;
import com.auction.matchschedule.enums.PlayoffFormat;
import com.auction.tournamentteam.entity.TournamentTeam;
import com.auction.matchschedule.engine.PlayoffFixtureGenerator;
import com.auction.matchschedule.engine.PlayoffFixtureGenerator.PlayoffMatch;

import com.auction.matchschedule.engine.PlayoffFixtureAllocator;
import com.auction.matchschedule.engine.PlayoffFixtureValidator;
import com.auction.matchschedule.engine.PlayoffFixtureAllocator.AllocatedPlayoffFixture;

@Service
@Transactional
public class MatchScheduleServiceImpl implements MatchScheduleService {

	// ==========================================
	// REPOSITORIES
	// ==========================================

	private final MatchScheduleRepository scheduleRepository;
	private final MatchScheduleFixtureRepository fixtureRepository;
	private final MatchScheduleTeamDrawRepository teamDrawRepository;
	private final MatchScheduleExcludedDateRepository excludedDateRepository;
	private final TournamentRepository tournamentRepository;

	// ==========================================
	// MAPPER
	// ==========================================

	private final MatchScheduleMapper mapper;

	// ==========================================
	// SCHEDULING ENGINES
	// ==========================================

	private final RandomTeamDrawGenerator randomTeamDrawGenerator;
	private final RoundRobinGenerator roundRobinGenerator;
	private final PartialRoundRobinGenerator partialRoundRobinGenerator;
	private final FairMatchOrderingEngine fairMatchOrderingEngine;
	private final AllowedPlayingDateGenerator allowedPlayingDateGenerator;
	private final MatchTimeSlotGenerator matchTimeSlotGenerator;
	private final CalendarFixtureAllocator calendarFixtureAllocator;
	private final AllocatedFixtureValidator allocatedFixtureValidator;
	private final PlayoffFixtureGenerator playoffFixtureGenerator;
	private final PlayoffFixtureAllocator playoffFixtureAllocator;
	private final PlayoffFixtureValidator playoffFixtureValidator;

	// ==========================================
	// CONSTRUCTOR
	// ==========================================

	public MatchScheduleServiceImpl(MatchScheduleRepository scheduleRepository,
			MatchScheduleFixtureRepository fixtureRepository, MatchScheduleTeamDrawRepository teamDrawRepository,
			MatchScheduleExcludedDateRepository excludedDateRepository, TournamentRepository tournamentRepository,
			MatchScheduleMapper mapper, RandomTeamDrawGenerator randomTeamDrawGenerator,
			RoundRobinGenerator roundRobinGenerator, PartialRoundRobinGenerator partialRoundRobinGenerator,
			FairMatchOrderingEngine fairMatchOrderingEngine, AllowedPlayingDateGenerator allowedPlayingDateGenerator,
			MatchTimeSlotGenerator matchTimeSlotGenerator, CalendarFixtureAllocator calendarFixtureAllocator,
			AllocatedFixtureValidator allocatedFixtureValidator, PlayoffFixtureGenerator playoffFixtureGenerator,
			PlayoffFixtureAllocator playoffFixtureAllocator, PlayoffFixtureValidator playoffFixtureValidator) {

		this.scheduleRepository = scheduleRepository;
		this.fixtureRepository = fixtureRepository;
		this.teamDrawRepository = teamDrawRepository;
		this.excludedDateRepository = excludedDateRepository;
		this.tournamentRepository = tournamentRepository;
		this.mapper = mapper;

		this.randomTeamDrawGenerator = randomTeamDrawGenerator;
		this.roundRobinGenerator = roundRobinGenerator;
		this.partialRoundRobinGenerator = partialRoundRobinGenerator;
		this.fairMatchOrderingEngine = fairMatchOrderingEngine;
		this.allowedPlayingDateGenerator = allowedPlayingDateGenerator;
		this.matchTimeSlotGenerator = matchTimeSlotGenerator;
		this.calendarFixtureAllocator = calendarFixtureAllocator;
		this.allocatedFixtureValidator = allocatedFixtureValidator;
		this.playoffFixtureGenerator = playoffFixtureGenerator;

		this.playoffFixtureAllocator = playoffFixtureAllocator;
		this.playoffFixtureValidator = playoffFixtureValidator;
	}

	// ==========================================
	// 1. CREATE DRAFT SCHEDULE
	// ==========================================

	@Override
	public MatchScheduleResponse createSchedule(MatchScheduleCreateRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Match schedule request is required.");
		}

		if (request.getTournamentId() == null) {
			throw new IllegalArgumentException("Tournament ID is required.");
		}

		if (request.getSchedulingType() == null) {
			throw new IllegalArgumentException("Scheduling type is required.");
		}

		// ==========================================
		// VALIDATE LEAGUE CONFIGURATION
		// ==========================================

		String normalizedLeagueFormat = validateLeagueConfiguration(request.getLeagueFormat(),
				request.getOpponentsToSkip());

		// ==========================================
		// VALIDATE PLAYOFF CONFIGURATION
		// ==========================================

		PlayoffFormat playoffFormat = validatePlayoffConfiguration(request.getPlayoffFormat());

		if (request.getStartDate() == null || request.getEndDate() == null
				|| request.getStartDate().isAfter(request.getEndDate())) {

			throw new IllegalArgumentException("Invalid schedule start date or end date.");
		}

		if (request.getDailyStartTime() == null || request.getDailyEndTime() == null
				|| !request.getDailyStartTime().isBefore(request.getDailyEndTime())) {

			throw new IllegalArgumentException("Daily start time must be before end time.");
		}

		Tournament tournament = tournamentRepository.findById(request.getTournamentId())
				.orElseThrow(() -> new IllegalArgumentException("Tournament not found: " + request.getTournamentId()));

		MatchSchedule schedule = new MatchSchedule();

		schedule.setTournament(tournament);
		schedule.setName(request.getName());

		schedule.setSchedulingType(request.getSchedulingType().name());

		schedule.setLeagueFormat(normalizedLeagueFormat);
		schedule.setOpponentsToSkip(request.getOpponentsToSkip());
		// ==========================================
		// PLAYOFF CONFIGURATION
		// ==========================================

		schedule.setPlayoffFormat(playoffFormat.name());

		schedule.setQualifyingTeamCount(playoffFormat.getQualifyingTeamCount());

		schedule.setStartDate(request.getStartDate());
		schedule.setEndDate(request.getEndDate());

		schedule.setAllowedDaysOfWeek(serializeDays(request.getAllowedDaysOfWeek()));

		schedule.setSelectedDates(serializeDates(request.getSelectedDates()));

		schedule.setDailyStartTime(request.getDailyStartTime());
		schedule.setDailyEndTime(request.getDailyEndTime());

		schedule.setMatchDurationMinutes(request.getMatchDurationMinutes());

		schedule.setBreakBetweenMatchesMinutes(request.getBreakBetweenMatchesMinutes());

		schedule.setMinimumRestMinutes(request.getMinimumRestMinutes());

		schedule.setMinimumMatchesBetweenAppearances(request.getMinimumMatchesBetweenAppearances());

		schedule.setMaxMatchesPerTeamPerDay(request.getMaxMatchesPerTeamPerDay());

		schedule.setNumberOfGrounds(request.getNumberOfGrounds());

		schedule.setRandomSeed(
				request.getRandomSeed() != null ? request.getRandomSeed() : ThreadLocalRandom.current().nextLong());

		schedule.setGenerationNumber(1);
		schedule.setStatus(MatchScheduleStatus.DRAFT);

		MatchSchedule saved = scheduleRepository.save(schedule);

		if (request.getExcludedDates() != null) {

			for (LocalDate date : request.getExcludedDates()) {

				if (date == null) {
					throw new IllegalArgumentException("Excluded dates cannot contain null.");
				}

				MatchScheduleExcludedDate excluded = new MatchScheduleExcludedDate();

				excluded.setMatchSchedule(saved);
				excluded.setExcludedDate(date);

				excludedDateRepository.save(excluded);
			}
		}

		return buildScheduleResponse(saved);
	}

	// ==========================================
	// 2. GET SCHEDULE BY ID
	// ==========================================

	@Override
	@Transactional(readOnly = true)
	public MatchScheduleResponse getScheduleById(UUID scheduleId) {

		MatchSchedule schedule = findSchedule(scheduleId);

		return buildScheduleResponse(schedule);
	}

	// ==========================================
	// 3. GET SCHEDULES BY TOURNAMENT
	// ==========================================

	@Override
	@Transactional(readOnly = true)
	public List<MatchScheduleResponse> getSchedulesByTournament(UUID tournamentId) {

		return scheduleRepository.findByTournamentIdOrderByCreatedAtDesc(tournamentId).stream()
				.map(this::buildScheduleResponse).toList();
	}

	// ==========================================
	// 4. GENERATE / REGENERATE SCHEDULE
	// ==========================================

	@Override
	public MatchScheduleGenerationResponse generateSchedule(UUID scheduleId, MatchScheduleGenerateRequest request) {

		MatchSchedule schedule = findSchedule(scheduleId);

		// Only draft schedules can be generated.
		if (schedule.getStatus() != MatchScheduleStatus.DRAFT) {
			throw new IllegalStateException("Only DRAFT schedules can be generated.");
		}

		boolean regenerate = request != null && Boolean.TRUE.equals(request.getRegenerate());

		long existingFixtures = fixtureRepository.countByMatchScheduleId(scheduleId);

		long existingDraws = teamDrawRepository.countByMatchScheduleId(scheduleId);

		boolean hasExistingData = existingFixtures > 0 || existingDraws > 0;

		if (hasExistingData && !regenerate) {
			throw new IllegalStateException(
					"This schedule already has generated data. " + "Set regenerate=true to replace it.");
		}

		// ==========================================
		// STEP 1: RESOLVE GENERATION SETTINGS
		// ==========================================

		long seed = request != null && request.getRandomSeed() != null ? request.getRandomSeed()
				: schedule.getRandomSeed();

		int minimumGap = request != null && request.getMinimumMatchesBetweenAppearances() != null
				? request.getMinimumMatchesBetweenAppearances()
				: schedule.getMinimumMatchesBetweenAppearances();

		if (minimumGap < 1) {

			throw new IllegalArgumentException("Minimum matches between appearances "
					+ "must be at least 1 for the current " + "fairness ordering engine.");
		}

		// ==========================================
		// STEP 1A: VALIDATE LEAGUE CONFIGURATION
		// ==========================================

		String leagueFormat = validateLeagueConfiguration(schedule.getLeagueFormat(), schedule.getOpponentsToSkip());

		// ==========================================
		// STEP 1B: VALIDATE PLAYOFF CONFIGURATION
		// ==========================================

		PlayoffFormat playoffFormat = validatePlayoffConfiguration(schedule.getPlayoffFormat());

		// Validate saved qualifying team count.

		if (!Integer.valueOf(playoffFormat.getQualifyingTeamCount()).equals(schedule.getQualifyingTeamCount())) {

			throw new IllegalStateException(
					"Invalid qualifying team count for playoff format: " + playoffFormat.name());
		}

		// ==========================================
		// STEP 2: RANDOM TEAM DRAW
		// ==========================================

		List<TournamentTeam> randomizedTeams = randomTeamDrawGenerator
				.generateRandomDraw(schedule.getTournament().getId(), seed);

		// ==========================================
		// STEP 2A: VALIDATE PARTIAL ROUND ROBIN
		// ==========================================

		// Validate against the actual active tournament teams.

		if ("PARTIAL_ROUND_ROBIN".equals(leagueFormat)) {

			validatePartialRoundRobinTeamCount(randomizedTeams.size(), schedule.getOpponentsToSkip());
		}

		// ==========================================
		// STEP 2B: VALIDATE PLAYOFF TEAM COUNT
		// ==========================================

		validatePlayoffTeamCount(playoffFormat, randomizedTeams.size());

		// ==========================================
		// STEP 3: GENERATE LEAGUE FIXTURES
		// ==========================================

		List<RoundRobinMatch> roundRobinMatches = switch (leagueFormat) {
		case "SINGLE_ROUND_ROBIN" -> roundRobinGenerator.generate(randomizedTeams, false);
		case "DOUBLE_ROUND_ROBIN" -> roundRobinGenerator.generate(randomizedTeams, true);
		case "PARTIAL_ROUND_ROBIN" ->
			partialRoundRobinGenerator.generate(randomizedTeams, schedule.getOpponentsToSkip(), seed);
		default -> throw new IllegalArgumentException("Unsupported league format: " + leagueFormat);
		};

		// ==========================================
		// STEP 3A: VALIDATE GENERATED LEAGUE PAIRINGS
		// ==========================================

		validateGeneratedLeaguePairings(randomizedTeams, roundRobinMatches, leagueFormat,
				schedule.getOpponentsToSkip());

		// ==========================================
		// STEP 4: APPLY FAIR MATCH ORDERING
		// ==========================================

		List<RoundRobinMatch> orderedMatches = fairMatchOrderingEngine.arrange(roundRobinMatches, seed);

		// ==========================================
		// STEP 5: BUILD CALENDAR CONFIGURATION
		// ==========================================

		SchedulingCalendarConfig config = buildCalendarConfig(schedule, minimumGap);

		// ==========================================
		// STEP 6: GENERATE PLAYING DATES
		// ==========================================

		List<LocalDate> playingDates = allowedPlayingDateGenerator.generate(config);

		// ==========================================
		// STEP 7: GENERATE TIME SLOTS
		// ==========================================

		List<MatchTimeSlot> availableSlots = matchTimeSlotGenerator.generate(playingDates, config);

		// ==========================================
		// STEP 8: ALLOCATE FIXTURES
		// ==========================================

		List<AllocatedFixture> allocatedFixtures = calendarFixtureAllocator.allocate(orderedMatches, availableSlots,
				config);

		// ==========================================
		// STEP 9: VALIDATE ALLOCATED FIXTURES
		// ==========================================

		allocatedFixtureValidator.validate(allocatedFixtures, config);

		// ==========================================
		// STEP 9A: GENERATE PLAYOFF FIXTURES
		// ==========================================

		int lastLeagueMatchNumber = allocatedFixtures.stream().mapToInt(AllocatedFixture::matchNumber).max().orElse(0);

		int lastLeagueRoundNumber = allocatedFixtures.stream().mapToInt(AllocatedFixture::roundNumber).max().orElse(0);

		List<PlayoffMatch> playoffMatches = playoffFixtureGenerator.generate(playoffFormat, lastLeagueMatchNumber,
				lastLeagueRoundNumber);

		// ==========================================
		// STEP 9B: ALLOCATE PLAYOFF FIXTURES
		// ==========================================

		List<AllocatedPlayoffFixture> allocatedPlayoffFixtures = playoffFixtureAllocator.allocate(playoffMatches,
				allocatedFixtures, availableSlots, config);

		// ==========================================
		// STEP 9C: VALIDATE ALLOCATED PLAYOFF FIXTURES
		// ==========================================

		playoffFixtureValidator.validate(playoffFormat, allocatedFixtures, allocatedPlayoffFixtures, config);

		// ==========================================
		// STEP 10: DELETE PREVIOUS GENERATED DATA
		// ==========================================

		// Generation and replacement happen in the
		// same transaction. A failure rolls back changes.

		if (hasExistingData && regenerate) {

			fixtureRepository.deleteByMatchScheduleId(scheduleId);

			teamDrawRepository.deleteByMatchScheduleId(scheduleId);

			// Ensure deletions are executed before
			// inserting replacement rows.
			fixtureRepository.flush();
			teamDrawRepository.flush();
		}

		// ==========================================
		// STEP 11: SAVE RANDOMIZED TEAM DRAWS
		// ==========================================

		List<MatchScheduleTeamDraw> draws = new ArrayList<>();

		for (int i = 0; i < randomizedTeams.size(); i++) {

			MatchScheduleTeamDraw draw = new MatchScheduleTeamDraw();

			draw.setMatchSchedule(schedule);

			draw.setTournamentTeam(randomizedTeams.get(i));

			draw.setRandomNumber(i + 1);

			draws.add(draw);
		}

		draws = teamDrawRepository.saveAll(draws);

		// ==========================================
		// STEP 12: BUILD TOURNAMENT TEAM LOOKUP
		// ==========================================

		Map<UUID, TournamentTeam> teamById = new HashMap<>();

		for (TournamentTeam team : randomizedTeams) {
			teamById.put(team.getId(), team);
		}

		// ==========================================
		// STEP 13: SAVE LEAGUE FIXTURES
		// ==========================================

		List<MatchScheduleFixture> fixtures = new ArrayList<>();

		for (AllocatedFixture allocated : allocatedFixtures) {

			TournamentTeam homeTeam = teamById.get(allocated.homeTournamentTeamId());

			TournamentTeam awayTeam = teamById.get(allocated.awayTournamentTeamId());

			if (homeTeam == null || awayTeam == null) {
				throw new IllegalStateException("Generated fixture references an " + "unknown tournament team.");
			}

			MatchScheduleFixture fixture = new MatchScheduleFixture();

			fixture.setMatchSchedule(schedule);

			fixture.setMatchNumber(allocated.matchNumber());

			fixture.setRoundNumber(allocated.roundNumber());

			fixture.setMatchStage("LEAGUE");

			fixture.setHomeTournamentTeam(homeTeam);
			fixture.setAwayTournamentTeam(awayTeam);

			fixture.setMatchDate(allocated.matchDate());

			fixture.setStartTime(allocated.startTime());

			fixture.setEndTime(allocated.endTime());

			fixture.setGroundNumber(allocated.groundNumber());

			fixture.setStatus("SCHEDULED");

			fixtures.add(fixture);
		}

		// ==========================================
		// STEP 13A: PREPARE PLAYOFF FIXTURES
		// ==========================================

		for (AllocatedPlayoffFixture allocated : allocatedPlayoffFixtures) {
			MatchScheduleFixture fixture = new MatchScheduleFixture();
			fixture.setMatchSchedule(schedule);
			fixture.setMatchNumber(allocated.matchNumber());
			fixture.setRoundNumber(allocated.roundNumber());
			fixture.setMatchStage(allocated.matchStage().name());

			// Actual teams are resolved after league standings and results.
			fixture.setHomeTournamentTeam(null);
			fixture.setAwayTournamentTeam(null);
			fixture.setHomeTeamSource(allocated.homeTeamSource());
			fixture.setAwayTeamSource(allocated.awayTeamSource());
			fixture.setMatchDate(allocated.matchDate());
			fixture.setStartTime(allocated.startTime());
			fixture.setEndTime(allocated.endTime());
			fixture.setGroundNumber(allocated.groundNumber());
			fixture.setStatus("SCHEDULED");
			fixtures.add(fixture);
		}

		// ==========================================
		// STEP 13B: PERSIST ALL GENERATED FIXTURES
		// ==========================================

		fixtures = fixtureRepository.saveAll(fixtures);

		// ==========================================
		// STEP 14: UPDATE GENERATION METADATA
		// ==========================================

		schedule.setRandomSeed(seed);

		schedule.setMinimumMatchesBetweenAppearances(minimumGap);

		if (hasExistingData && regenerate) {

			schedule.setGenerationNumber(schedule.getGenerationNumber() + 1);
		}

		scheduleRepository.save(schedule);

		// Flush before preparing the response so that
		// database constraint violations surface here.
		teamDrawRepository.flush();
		fixtureRepository.flush();
		scheduleRepository.flush();

		// ==========================================
		// STEP 15: PREPARE GENERATION RESPONSE
		// ==========================================

		MatchScheduleGenerationResponse response = new MatchScheduleGenerationResponse();

		response.setMatchScheduleId(schedule.getId());

		response.setTournamentId(schedule.getTournament().getId());

		response.setScheduleName(schedule.getName());

		response.setStatus(schedule.getStatus());

		response.setGenerationNumber(schedule.getGenerationNumber());

		response.setRandomSeed(seed);

		response.setRegenerated(hasExistingData && regenerate);

		response.setTotalTeams(draws.size());

		response.setTotalLeagueMatches(allocatedFixtures.size());
		response.setTotalPlayoffMatches(allocatedPlayoffFixtures.size());

		response.setTotalMatches(fixtures.size());

		response.setTeamDraws(mapper.toTeamDrawResponses(draws));

		response.setFixtures(mapper.toFixtureResponses(fixtures));

		response.setGeneratedAt(LocalDateTime.now());

		return response;
	}

	// ==========================================
	// 5. GET FIXTURES
	// ==========================================

	@Override
	@Transactional(readOnly = true)
	public List<MatchScheduleFixtureResponse> getFixtures(UUID scheduleId) {

		findSchedule(scheduleId);

		return mapper.toFixtureResponses(fixtureRepository.findByMatchScheduleIdOrderByMatchNumberAsc(scheduleId));
	}

	// ==========================================
	// 6. GET TEAM DRAWS
	// ==========================================

	@Override
	@Transactional(readOnly = true)
	public List<MatchScheduleTeamDrawResponse> getTeamDraws(UUID scheduleId) {

		findSchedule(scheduleId);

		return mapper.toTeamDrawResponses(teamDrawRepository.findByMatchScheduleIdOrderByRandomNumberAsc(scheduleId));
	}

	// ==========================================
	// 7. PUBLISH SCHEDULE
	// ==========================================

	@Override
	public MatchScheduleResponse publishSchedule(UUID scheduleId) {

		throw new UnsupportedOperationException("Schedule publication is not implemented yet.");
	}

	// ==========================================
	// 8. CANCEL SCHEDULE
	// ==========================================

	@Override
	public MatchScheduleResponse cancelSchedule(UUID scheduleId) {

		throw new UnsupportedOperationException("Schedule cancellation is not implemented yet.");
	}

	// ==========================================
	// 9. DELETE SCHEDULE
	// ==========================================

	@Override
	public void deleteSchedule(UUID scheduleId) {

		throw new UnsupportedOperationException("Schedule deletion is not implemented yet.");
	}

	// ==========================================
	// 10. FIND SCHEDULE
	// ==========================================

	private MatchSchedule findSchedule(UUID scheduleId) {

		return scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new IllegalArgumentException("Match schedule not found: " + scheduleId));
	}

	// ==========================================
	// 11. BUILD SCHEDULE RESPONSE
	// ==========================================

	// ==========================================
	// 11. BUILD SCHEDULE RESPONSE
	// ==========================================

	private MatchScheduleResponse buildScheduleResponse(MatchSchedule schedule) {

		MatchScheduleResponse response = mapper.toResponse(schedule);

		// ==========================================
		// EXCLUDED DATES
		// ==========================================

		Set<LocalDate> excludedDates = excludedDateRepository
				.findByMatchScheduleIdOrderByExcludedDateAsc(schedule.getId()).stream()
				.map(MatchScheduleExcludedDate::getExcludedDate).collect(Collectors.toSet());

		response.setExcludedDates(excludedDates);

		// ==========================================
		// TOTAL PARTICIPATING TEAMS
		// ==========================================

		response.setTotalTeams(Math.toIntExact(teamDrawRepository.countByMatchScheduleId(schedule.getId())));

		// ==========================================
		// FETCH GENERATED FIXTURES
		// ==========================================

		List<MatchScheduleFixture> fixtures = fixtureRepository
				.findByMatchScheduleIdOrderByMatchNumberAsc(schedule.getId());

		// ==========================================
		// COUNT LEAGUE MATCHES
		// ==========================================

		int totalLeagueMatches = Math.toIntExact(
				fixtures.stream().filter(fixture -> MatchStage.fromValue(fixture.getMatchStage()).isLeague()).count());

		// ==========================================
		// COUNT PLAYOFF MATCHES
		// ==========================================

		int totalPlayoffMatches = Math.toIntExact(
				fixtures.stream().filter(fixture -> MatchStage.fromValue(fixture.getMatchStage()).isPlayoff()).count());

		// ==========================================
		// SET MATCH COUNTS
		// ==========================================

		response.setTotalLeagueMatches(totalLeagueMatches);

		response.setTotalPlayoffMatches(totalPlayoffMatches);

		response.setTotalMatches(fixtures.size());

		return response;
	}

	// ==========================================
	// 12. BUILD CALENDAR CONFIGURATION
	// ==========================================

	private SchedulingCalendarConfig buildCalendarConfig(MatchSchedule schedule, int minimumGap) {

		SchedulingCalendarConfig config = new SchedulingCalendarConfig();

		config.setSchedulingType(SchedulingType.valueOf(schedule.getSchedulingType()));

		config.setStartDate(schedule.getStartDate());

		config.setEndDate(schedule.getEndDate());

		// ------------------------------------------
		// ALLOWED WEEKDAYS
		// ------------------------------------------

		Set<DayOfWeek> allowedDays = new HashSet<>();

		if (schedule.getAllowedDaysOfWeek() != null && !schedule.getAllowedDaysOfWeek().isBlank()) {

			for (String day : schedule.getAllowedDaysOfWeek().split(",")) {

				allowedDays.add(DayOfWeek.valueOf(day.trim()));
			}
		}

		config.setAllowedDaysOfWeek(allowedDays);

		// ------------------------------------------
		// SELECTED PLAYING DATES
		// ------------------------------------------

		Set<LocalDate> selectedDates = new HashSet<>();

		if (schedule.getSelectedDates() != null && !schedule.getSelectedDates().isBlank()) {

			for (String date : schedule.getSelectedDates().split(",")) {

				selectedDates.add(LocalDate.parse(date.trim()));
			}
		}

		config.setSelectedDates(selectedDates);

		// ------------------------------------------
		// EXCLUDED DATES
		// ------------------------------------------

		Set<LocalDate> excludedDates = new HashSet<>();

		List<MatchScheduleExcludedDate> exclusions = excludedDateRepository
				.findByMatchScheduleIdOrderByExcludedDateAsc(schedule.getId());

		for (MatchScheduleExcludedDate excluded : exclusions) {

			excludedDates.add(excluded.getExcludedDate());
		}

		config.setExcludedDates(excludedDates);

		// ------------------------------------------
		// DAILY MATCH SETTINGS
		// ------------------------------------------

		config.setDailyStartTime(schedule.getDailyStartTime());

		config.setDailyEndTime(schedule.getDailyEndTime());

		config.setMatchDurationMinutes(schedule.getMatchDurationMinutes());

		config.setBreakBetweenMatchesMinutes(schedule.getBreakBetweenMatchesMinutes());

		config.setMinimumRestMinutes(schedule.getMinimumRestMinutes());

		// ------------------------------------------
		// FAIRNESS SETTINGS
		// ------------------------------------------

		config.setMinimumMatchesBetweenAppearances(minimumGap);

		config.setMaxMatchesPerTeamPerDay(schedule.getMaxMatchesPerTeamPerDay());

		// ------------------------------------------
		// GROUND SETTINGS
		// ------------------------------------------

		config.setNumberOfGrounds(schedule.getNumberOfGrounds());

		return config;
	}

	// ==========================================
	// 13. SERIALIZE ALLOWED DAYS
	// ==========================================

	private String serializeDays(Set<DayOfWeek> days) {

		if (days == null || days.isEmpty()) {
			return null;
		}

		return days.stream().sorted().map(DayOfWeek::name).collect(Collectors.joining(","));
	}

	// ==========================================
	// 14. SERIALIZE SELECTED DATES
	// ==========================================

	private String serializeDates(Set<LocalDate> dates) {

		if (dates == null || dates.isEmpty()) {
			return null;
		}

		return dates.stream().sorted().map(LocalDate::toString).collect(Collectors.joining(","));
	}

	// ==========================================
	// 15. VALIDATE LEAGUE CONFIGURATION
	// ==========================================

	private String validateLeagueConfiguration(String leagueFormat, Integer opponentsToSkip) {

		if (leagueFormat == null || leagueFormat.isBlank()) {
			throw new IllegalArgumentException("League format is required.");
		}

		String normalizedLeagueFormat = leagueFormat.trim().toUpperCase(Locale.ROOT);

		if (opponentsToSkip == null || opponentsToSkip < 0) {
			throw new IllegalArgumentException("Opponents to skip must be zero or positive.");
		}

		switch (normalizedLeagueFormat) {
		case "SINGLE_ROUND_ROBIN":
		case "DOUBLE_ROUND_ROBIN":
			if (opponentsToSkip != 0) {
				throw new IllegalArgumentException(
						"Opponents to skip must be zero for " + normalizedLeagueFormat + ".");
			}
			break;

		case "PARTIAL_ROUND_ROBIN":
			if (opponentsToSkip < 1) {
				throw new IllegalArgumentException(
						"Opponents to skip must be at least 1 " + "for Partial Round Robin.");
			}
			break;

		default:
			throw new IllegalArgumentException("Unsupported league format: " + normalizedLeagueFormat);
		}

		return normalizedLeagueFormat;
	}

	// ==========================================
	// VALIDATE PLAYOFF CONFIGURATION
	// ==========================================

	private PlayoffFormat validatePlayoffConfiguration(String playoffFormat) {

		return PlayoffFormat.fromValue(playoffFormat);
	}

	// ==========================================
	// VALIDATE PLAYOFF TEAM COUNT
	// ==========================================

	private void validatePlayoffTeamCount(PlayoffFormat playoffFormat, int totalTeams) {

		playoffFormat.validateTeamCount(totalTeams);
	}

	// ==========================================
	// 16. VALIDATE PARTIAL ROUND ROBIN TEAM COUNT
	// ==========================================

	private void validatePartialRoundRobinTeamCount(int totalTeams, Integer opponentsToSkip) {

		if (totalTeams < 3) {
			throw new IllegalArgumentException(
					"At least three active teams are required " + "for Partial Round Robin.");
		}

		if (opponentsToSkip == null || opponentsToSkip < 1) {
			throw new IllegalArgumentException("Opponents to skip must be at least 1.");
		}

		int maximumOpponentsToSkip = totalTeams - 2;

		if (opponentsToSkip > maximumOpponentsToSkip) {
			throw new IllegalArgumentException("Opponents to skip cannot exceed " + maximumOpponentsToSkip + " for "
					+ totalTeams + " active teams.");
		}

		int matchesPerTeam = totalTeams - 1 - opponentsToSkip;
		long totalAppearances = (long) totalTeams * matchesPerTeam;

		if (totalAppearances % 2 != 0) {
			throw new IllegalArgumentException(
					"Invalid Partial Round Robin configuration. " + totalTeams + " teams cannot each play "
							+ matchesPerTeam + " matches equally. " + "Please change the opponents to skip.");
		}
	}

	// ==========================================
	// 17. VALIDATE GENERATED LEAGUE PAIRINGS
	// ==========================================

	private void validateGeneratedLeaguePairings(List<TournamentTeam> randomizedTeams, List<RoundRobinMatch> matches,
			String leagueFormat, Integer opponentsToSkip) {

		int totalTeams = randomizedTeams.size();

		// ==========================================
		// EXPECTED MATCHES PER TEAM
		// ==========================================

		int expectedMatchesPerTeam = switch (leagueFormat) {

		case "SINGLE_ROUND_ROBIN" -> totalTeams - 1;

		case "DOUBLE_ROUND_ROBIN" -> (totalTeams - 1) * 2;

		case "PARTIAL_ROUND_ROBIN" -> totalTeams - 1 - opponentsToSkip;

		default -> throw new IllegalArgumentException("Unsupported league format: " + leagueFormat);
		};

		long expectedTotalMatches = (long) totalTeams * expectedMatchesPerTeam / 2;

		// ==========================================
		// VALIDATE TOTAL FIXTURE COUNT
		// ==========================================

		if (matches.size() != expectedTotalMatches) {

			throw new IllegalStateException("Invalid generated fixture count. Expected: " + expectedTotalMatches
					+ ", Actual: " + matches.size());
		}

		// ==========================================
		// INITIALIZE TEAM MATCH COUNTS
		// ==========================================

		Map<UUID, Integer> teamMatchCounts = new HashMap<>();

		Map<String, Integer> pairingCounts = new HashMap<>();

		for (TournamentTeam team : randomizedTeams) {

			teamMatchCounts.put(team.getId(), 0);
		}

		int maximumMatchesPerPair = "DOUBLE_ROUND_ROBIN".equals(leagueFormat) ? 2 : 1;

		// ==========================================
		// VALIDATE EACH GENERATED MATCH
		// ==========================================

		for (RoundRobinMatch match : matches) {

			if (match == null || match.homeTeam() == null || match.awayTeam() == null) {

				throw new IllegalStateException("Generated fixture contains invalid teams.");
			}

			UUID homeTeamId = match.homeTeam().getId();

			UUID awayTeamId = match.awayTeam().getId();

			// Prevent unknown tournament teams.

			if (!teamMatchCounts.containsKey(homeTeamId) || !teamMatchCounts.containsKey(awayTeamId)) {

				throw new IllegalStateException("Generated fixture references an " + "unknown tournament team.");
			}

			// Prevent a team playing against itself.

			if (homeTeamId.equals(awayTeamId)) {

				throw new IllegalStateException("A team cannot play against itself: " + homeTeamId);
			}

			// Count matches for both teams.

			teamMatchCounts.merge(homeTeamId, 1, Integer::sum);

			teamMatchCounts.merge(awayTeamId, 1, Integer::sum);

			// Create a direction-independent pairing key.

			String pairingKey = homeTeamId.compareTo(awayTeamId) < 0 ? homeTeamId + ":" + awayTeamId
					: awayTeamId + ":" + homeTeamId;

			int pairingCount = pairingCounts.merge(pairingKey, 1, Integer::sum);

			// Prevent duplicate or excessive pairings.

			if (pairingCount > maximumMatchesPerPair) {

				throw new IllegalStateException("Duplicate or excessive league pairing: " + pairingKey);
			}
		}

		// ==========================================
		// VALIDATE MATCH COUNT FOR EVERY TEAM
		// ==========================================

		for (Map.Entry<UUID, Integer> entry : teamMatchCounts.entrySet()) {

			if (entry.getValue() != expectedMatchesPerTeam) {

				throw new IllegalStateException("Invalid match count for team " + entry.getKey() + ". Expected: "
						+ expectedMatchesPerTeam + ", Actual: " + entry.getValue());
			}
		}
	}
}
