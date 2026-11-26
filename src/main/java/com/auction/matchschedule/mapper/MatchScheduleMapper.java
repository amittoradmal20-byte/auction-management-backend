
package com.auction.matchschedule.mapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.auction.matchschedule.dto.response.MatchScheduleFixtureResponse;
import com.auction.matchschedule.dto.response.MatchScheduleResponse;
import com.auction.matchschedule.dto.response.MatchScheduleTeamDrawResponse;

import com.auction.matchschedule.entity.MatchSchedule;
import com.auction.matchschedule.entity.MatchScheduleFixture;
import com.auction.matchschedule.entity.MatchScheduleTeamDraw;

import com.auction.matchschedule.enums.SchedulingType;

import com.auction.team.entity.Team;
import com.auction.tournamentteam.entity.TournamentTeam;

@Component
public class MatchScheduleMapper {

    // ==========================================
    // 1. MATCH SCHEDULE RESPONSE
    // ==========================================

    public MatchScheduleResponse toResponse(
            MatchSchedule entity) {

        if (entity == null) {
            return null;
        }

        MatchScheduleResponse response =
                new MatchScheduleResponse();

        response.setId(entity.getId());

        // ==========================================
        // TOURNAMENT INFORMATION
        // ==========================================

        if (entity.getTournament() != null) {

            response.setTournamentId(
                    entity.getTournament().getId()
            );

            response.setTournamentName(
                    entity.getTournament().getName()
            );

            response.setTournamentLogoUrl(
                    entity.getTournament().getLogoUrl()
            );
        }

        response.setName(entity.getName());
        response.setStatus(entity.getStatus());

        if (entity.getSchedulingType() != null) {
            response.setSchedulingType(
                    SchedulingType.valueOf(
                            entity.getSchedulingType()
                    )
            );
        }

        response.setLeagueFormat(
                entity.getLeagueFormat()
        );

        response.setOpponentsToSkip(
                entity.getOpponentsToSkip()
        );

        response.setPlayoffFormat(
                entity.getPlayoffFormat()
        );

        response.setQualifyingTeamCount(
                entity.getQualifyingTeamCount()
        );

        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());

        // ==========================================
        // CALENDAR CONFIGURATION
        // ==========================================

        response.setAllowedDaysOfWeek(
                parseAllowedDaysOfWeek(
                        entity.getAllowedDaysOfWeek()
                )
        );

        response.setSelectedDates(
                parseSelectedDates(
                        entity.getSelectedDates()
                )
        );

        response.setDailyStartTime(
                entity.getDailyStartTime()
        );

        response.setDailyEndTime(
                entity.getDailyEndTime()
        );

        response.setMatchDurationMinutes(
                entity.getMatchDurationMinutes()
        );

        response.setBreakBetweenMatchesMinutes(
                entity.getBreakBetweenMatchesMinutes()
        );

        response.setMinimumRestMinutes(
                entity.getMinimumRestMinutes()
        );

        response.setMinimumMatchesBetweenAppearances(
                entity.getMinimumMatchesBetweenAppearances()
        );

        response.setMaxMatchesPerTeamPerDay(
                entity.getMaxMatchesPerTeamPerDay()
        );

        response.setNumberOfGrounds(
                entity.getNumberOfGrounds()
        );

        response.setRandomSeed(
                entity.getRandomSeed()
        );

        response.setGenerationNumber(
                entity.getGenerationNumber()
        );

        response.setCreatedAt(
                entity.getCreatedAt()
        );

        response.setUpdatedAt(
                entity.getUpdatedAt()
        );

        return response;
    }

    // ==========================================
    // 2. MATCH FIXTURE RESPONSE
    // ==========================================

    public MatchScheduleFixtureResponse toFixtureResponse(
            MatchScheduleFixture entity) {

        if (entity == null) {
            return null;
        }

        MatchScheduleFixtureResponse response =
                new MatchScheduleFixtureResponse();

        response.setId(entity.getId());

        if (entity.getMatchSchedule() != null) {
            response.setMatchScheduleId(
                    entity.getMatchSchedule().getId()
            );
        }

        response.setMatchNumber(
                entity.getMatchNumber()
        );

        response.setRoundNumber(
                entity.getRoundNumber()
        );

        response.setMatchStage(
                entity.getMatchStage()
        );

        response.setStatus(
                entity.getStatus()
        );

        // ==========================================
        // HOME TEAM
        // ==========================================

        TournamentTeam homeTournamentTeam =
                entity.getHomeTournamentTeam();

        if (homeTournamentTeam != null) {

            response.setHomeTournamentTeamId(
                    homeTournamentTeam.getId()
            );

            Team homeTeam =
                    homeTournamentTeam.getTeam();

            if (homeTeam != null) {

                response.setHomeTeamName(
                        homeTeam.getName()
                );

                response.setHomeTeamShortName(
                        homeTeam.getShortName()
                );

                response.setHomeTeamLogoUrl(
                        homeTeam.getLogoUrl()
                );
            }
        }

        // ==========================================
        // AWAY TEAM
        // ==========================================

        TournamentTeam awayTournamentTeam =
                entity.getAwayTournamentTeam();

        if (awayTournamentTeam != null) {

            response.setAwayTournamentTeamId(
                    awayTournamentTeam.getId()
            );

            Team awayTeam =
                    awayTournamentTeam.getTeam();

            if (awayTeam != null) {

                response.setAwayTeamName(
                        awayTeam.getName()
                );

                response.setAwayTeamShortName(
                        awayTeam.getShortName()
                );

                response.setAwayTeamLogoUrl(
                        awayTeam.getLogoUrl()
                );
            }
        }

        // ==========================================
        // PLAYOFF TEAM SOURCES
        // ==========================================

        response.setHomeTeamSource(
                entity.getHomeTeamSource()
        );

        response.setAwayTeamSource(
                entity.getAwayTeamSource()
        );

        // ==========================================
        // DATE AND TIME
        // ==========================================

        response.setMatchDate(
                entity.getMatchDate()
        );

        response.setStartTime(
                entity.getStartTime()
        );

        response.setEndTime(
                entity.getEndTime()
        );

        response.setGroundNumber(
                entity.getGroundNumber()
        );

        response.setCreatedAt(
                entity.getCreatedAt()
        );

        response.setUpdatedAt(
                entity.getUpdatedAt()
        );

        return response;
    }

    // ==========================================
    // 3. TEAM DRAW RESPONSE
    // ==========================================

    public MatchScheduleTeamDrawResponse toTeamDrawResponse(
            MatchScheduleTeamDraw entity) {

        if (entity == null) {
            return null;
        }

        MatchScheduleTeamDrawResponse response =
                new MatchScheduleTeamDrawResponse();

        response.setId(entity.getId());

        if (entity.getMatchSchedule() != null) {
            response.setMatchScheduleId(
                    entity.getMatchSchedule().getId()
            );
        }

        TournamentTeam tournamentTeam =
                entity.getTournamentTeam();

        if (tournamentTeam != null) {

            response.setTournamentTeamId(
                    tournamentTeam.getId()
            );

            Team team = tournamentTeam.getTeam();

            if (team != null) {

                response.setTeamId(
                        team.getId()
                );

                response.setTeamName(
                        team.getName()
                );

                response.setTeamShortName(
                        team.getShortName()
                );
            }
        }

        response.setRandomNumber(
                entity.getRandomNumber()
        );

        response.setCreatedAt(
                entity.getCreatedAt()
        );

        response.setUpdatedAt(
                entity.getUpdatedAt()
        );

        return response;
    }

    // ==========================================
    // 4. FIXTURE LIST MAPPING
    // ==========================================

    public List<MatchScheduleFixtureResponse> toFixtureResponses(
            List<MatchScheduleFixture> entities) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toFixtureResponse)
                .toList();
    }

    // ==========================================
    // 5. TEAM DRAW LIST MAPPING
    // ==========================================

    public List<MatchScheduleTeamDrawResponse> toTeamDrawResponses(
            List<MatchScheduleTeamDraw> entities) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toTeamDrawResponse)
                .toList();
    }

    // ==========================================
    // 6. PARSE ALLOWED WEEKDAYS
    // ==========================================

    private Set<DayOfWeek> parseAllowedDaysOfWeek(
            String value) {

        if (value == null || value.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(day -> !day.isEmpty())
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }

    // ==========================================
    // 7. PARSE SELECTED DATES
    // ==========================================

    private Set<LocalDate> parseSelectedDates(
            String value) {

        if (value == null || value.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(date -> !date.isEmpty())
                .map(LocalDate::parse)
                .collect(Collectors.toSet());
    }
}
