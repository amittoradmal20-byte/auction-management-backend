
package com.auction.matchschedule.service;

import java.util.List;
import java.util.UUID;

import com.auction.matchschedule.dto.request.MatchScheduleCreateRequest;
import com.auction.matchschedule.dto.request.MatchScheduleGenerateRequest;

import com.auction.matchschedule.dto.response.MatchScheduleResponse;
import com.auction.matchschedule.dto.response.MatchScheduleGenerationResponse;
import com.auction.matchschedule.dto.response.MatchScheduleFixtureResponse;
import com.auction.matchschedule.dto.response.MatchScheduleTeamDrawResponse;

public interface MatchScheduleService {

    // ==========================================
    // 1. CREATE MATCH SCHEDULE
    // ==========================================

    MatchScheduleResponse createSchedule(
            MatchScheduleCreateRequest request
    );

    // ==========================================
    // 2. GET MATCH SCHEDULE BY ID
    // ==========================================

    MatchScheduleResponse getScheduleById(
            UUID scheduleId
    );

    // ==========================================
    // 3. GET TOURNAMENT MATCH SCHEDULES
    // ==========================================

    List<MatchScheduleResponse> getSchedulesByTournament(
            UUID tournamentId
    );

    // ==========================================
    // 4. GENERATE / REGENERATE MATCH SCHEDULE
    // ==========================================

    MatchScheduleGenerationResponse generateSchedule(
            UUID scheduleId,
            MatchScheduleGenerateRequest request
    );

    // ==========================================
    // 5. GET GENERATED FIXTURES
    // ==========================================

    List<MatchScheduleFixtureResponse> getFixtures(
            UUID scheduleId
    );

    // ==========================================
    // 6. GET RANDOMIZED TEAM DRAWS
    // ==========================================

    List<MatchScheduleTeamDrawResponse> getTeamDraws(
            UUID scheduleId
    );

    // ==========================================
    // 7. PUBLISH MATCH SCHEDULE
    // ==========================================

    MatchScheduleResponse publishSchedule(
            UUID scheduleId
    );

    // ==========================================
    // 8. CANCEL MATCH SCHEDULE
    // ==========================================

    MatchScheduleResponse cancelSchedule(
            UUID scheduleId
    );

    // ==========================================
    // 9. DELETE DRAFT MATCH SCHEDULE
    // ==========================================

    void deleteSchedule(
            UUID scheduleId
    );

}
