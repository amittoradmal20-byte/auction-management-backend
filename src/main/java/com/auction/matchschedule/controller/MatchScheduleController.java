
package com.auction.matchschedule.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.auction.matchschedule.dto.request.MatchScheduleCreateRequest;
import com.auction.matchschedule.dto.request.MatchScheduleGenerateRequest;

import com.auction.matchschedule.dto.response.MatchScheduleFixtureResponse;
import com.auction.matchschedule.dto.response.MatchScheduleGenerationResponse;
import com.auction.matchschedule.dto.response.MatchScheduleResponse;
import com.auction.matchschedule.dto.response.MatchScheduleTeamDrawResponse;

import com.auction.matchschedule.service.MatchScheduleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/match-schedules")
@Validated
public class MatchScheduleController {

    private final MatchScheduleService matchScheduleService;

    public MatchScheduleController(
            MatchScheduleService matchScheduleService) {

        this.matchScheduleService = matchScheduleService;
    }

    // ==========================================
    // 1. CREATE MATCH SCHEDULE
    // ==========================================

    @PostMapping
    public ResponseEntity<MatchScheduleResponse> createSchedule(
            @Valid @RequestBody MatchScheduleCreateRequest request) {

        MatchScheduleResponse response =
                matchScheduleService.createSchedule(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================
    // 2. GET MATCH SCHEDULE BY ID
    // ==========================================

    @GetMapping("/{scheduleId}")
    public ResponseEntity<MatchScheduleResponse> getScheduleById(
            @PathVariable UUID scheduleId) {

        MatchScheduleResponse response =
                matchScheduleService.getScheduleById(scheduleId);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 3. GET MATCH SCHEDULES BY TOURNAMENT
    // ==========================================

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<MatchScheduleResponse>>
            getSchedulesByTournament(
                    @PathVariable UUID tournamentId) {

        List<MatchScheduleResponse> responses =
                matchScheduleService.getSchedulesByTournament(
                        tournamentId
                );

        return ResponseEntity.ok(responses);
    }

    // ==========================================
    // 4. GENERATE / REGENERATE FIXTURES
    // ==========================================

    @PostMapping("/{scheduleId}/generate")
    public ResponseEntity<MatchScheduleGenerationResponse>
            generateSchedule(
                    @PathVariable UUID scheduleId,
                    @RequestBody(required = false)
                    MatchScheduleGenerateRequest request) {

        MatchScheduleGenerationResponse response =
                matchScheduleService.generateSchedule(
                        scheduleId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 5. GET GENERATED FIXTURES
    // ==========================================

    @GetMapping("/{scheduleId}/fixtures")
    public ResponseEntity<List<MatchScheduleFixtureResponse>>
            getFixtures(
                    @PathVariable UUID scheduleId) {

        List<MatchScheduleFixtureResponse> responses =
                matchScheduleService.getFixtures(scheduleId);

        return ResponseEntity.ok(responses);
    }

    // ==========================================
    // 6. GET RANDOMIZED TEAM DRAWS
    // ==========================================

    @GetMapping("/{scheduleId}/team-draws")
    public ResponseEntity<List<MatchScheduleTeamDrawResponse>>
            getTeamDraws(
                    @PathVariable UUID scheduleId) {

        List<MatchScheduleTeamDrawResponse> responses =
                matchScheduleService.getTeamDraws(scheduleId);

        return ResponseEntity.ok(responses);
    }

    // ==========================================
    // 7. PUBLISH SCHEDULE
    // ==========================================

    @PostMapping("/{scheduleId}/publish")
    public ResponseEntity<MatchScheduleResponse> publishSchedule(
            @PathVariable UUID scheduleId) {

        MatchScheduleResponse response =
                matchScheduleService.publishSchedule(scheduleId);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 8. CANCEL SCHEDULE
    // ==========================================

    @PostMapping("/{scheduleId}/cancel")
    public ResponseEntity<MatchScheduleResponse> cancelSchedule(
            @PathVariable UUID scheduleId) {

        MatchScheduleResponse response =
                matchScheduleService.cancelSchedule(scheduleId);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 9. DELETE SCHEDULE
    // ==========================================

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable UUID scheduleId) {

        matchScheduleService.deleteSchedule(scheduleId);

        return ResponseEntity.noContent().build();
    }
}
