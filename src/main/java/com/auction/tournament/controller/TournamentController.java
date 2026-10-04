package com.auction.tournament.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auction.tournament.dto.TournamentCreateRequest;
import com.auction.tournament.dto.TournamentResponse;
import com.auction.tournament.dto.TournamentUpdateRequest;
import com.auction.tournament.service.TournamentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    
    private final  TournamentService tournamentService;

    public TournamentController( TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(
            @Valid @RequestBody TournamentCreateRequest request) {

        TournamentResponse response =
                tournamentService.createTournament(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournament(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                tournamentService.getTournament(id));
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {

        return ResponseEntity.ok(
                tournamentService.getAllTournaments());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable UUID id,
            @Valid @RequestBody TournamentUpdateRequest request) {

        return ResponseEntity.ok(
                tournamentService.updateTournament(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(
            @PathVariable UUID id) {

        tournamentService.deleteTournament(id);

        return ResponseEntity.noContent().build();
    }
}