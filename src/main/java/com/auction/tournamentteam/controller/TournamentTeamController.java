package com.auction.tournamentteam.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auction.tournamentteam.dto.TournamentTeamCreateRequest;
import com.auction.tournamentteam.dto.TournamentTeamResponse;
import com.auction.tournamentteam.dto.TournamentTeamUpdateRequest;
import com.auction.tournamentteam.service.TournamentTeamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tournament-teams")
public class TournamentTeamController {

    private final TournamentTeamService tournamentTeamService;

    public TournamentTeamController(
            TournamentTeamService tournamentTeamService) {

        this.tournamentTeamService = tournamentTeamService;
    }

    @PostMapping
    public ResponseEntity<TournamentTeamResponse> createTournamentTeam(
            @Valid @RequestBody TournamentTeamCreateRequest request) {

        TournamentTeamResponse response =
                tournamentTeamService.createTournamentTeam(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentTeamResponse> getTournamentTeam(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                tournamentTeamService.getTournamentTeam(id));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<TournamentTeamResponse>> getTeamsByTournament(
            @PathVariable UUID tournamentId) {

        return ResponseEntity.ok(
                tournamentTeamService
                        .getTeamsByTournament(tournamentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentTeamResponse> updateTournamentTeam(
            @PathVariable UUID id,
            @Valid @RequestBody TournamentTeamUpdateRequest request) {

        return ResponseEntity.ok(
                tournamentTeamService.updateTournamentTeam(
                        id,
                        request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournamentTeam(
            @PathVariable UUID id) {

        tournamentTeamService.deleteTournamentTeam(id);

        return ResponseEntity.noContent().build();
    }
}