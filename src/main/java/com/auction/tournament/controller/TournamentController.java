
package com.auction.tournament.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.auction.tournament.dto.TournamentCreateRequest;
import com.auction.tournament.dto.TournamentResponse;
import com.auction.tournament.dto.TournamentUpdateRequest;
import com.auction.tournament.service.TournamentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public TournamentController(
            TournamentService tournamentService) {

        this.tournamentService = tournamentService;
    }

    // ==========================================
    // CREATE TOURNAMENT
    // ==========================================

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(
            @Valid @RequestBody TournamentCreateRequest request) {

        TournamentResponse response =
                tournamentService.createTournament(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================
    // GET TOURNAMENT BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournament(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                tournamentService.getTournament(id));
    }

    // ==========================================
    // GET ALL TOURNAMENTS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<TournamentResponse>>
            getAllTournaments() {

        return ResponseEntity.ok(
                tournamentService.getAllTournaments());
    }

    // ==========================================
    // UPDATE TOURNAMENT
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable UUID id,
            @Valid @RequestBody TournamentUpdateRequest request) {

        return ResponseEntity.ok(
                tournamentService.updateTournament(id, request));
    }

    // ==========================================
    // DELETE TOURNAMENT
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(
            @PathVariable UUID id) {

        tournamentService.deleteTournament(id);

        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // OPEN TOURNAMENT REGISTRATION
    // ==========================================

    @PatchMapping("/{id}/open-registration")
    public ResponseEntity<TournamentResponse> openRegistration(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                tournamentService.openRegistration(id));
    }

    // ==========================================
    // CLOSE TOURNAMENT REGISTRATION
    // ==========================================

    @PatchMapping("/{id}/close-registration")
    public ResponseEntity<TournamentResponse> closeRegistration(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                tournamentService.closeRegistration(id));
    }

    // ==========================================
    // PREPARE TOURNAMENT AUCTION
    // ==========================================

    @PatchMapping("/{id}/prepare-auction")
    public ResponseEntity<TournamentResponse> prepareAuction(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                tournamentService.prepareAuction(id));
    }

    // ==========================================
    // UPLOAD OR REPLACE TOURNAMENT LOGO
    // ==========================================

    @PostMapping(
            value = "/{id}/logo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<TournamentResponse> uploadLogo(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        TournamentResponse response =
                tournamentService.uploadLogo(id, file);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // DELETE TOURNAMENT LOGO
    // ==========================================

    @DeleteMapping("/{id}/logo")
    public ResponseEntity<TournamentResponse> deleteLogo(
            @PathVariable UUID id) {

        TournamentResponse response =
                tournamentService.deleteLogo(id);

        return ResponseEntity.ok(response);
    }
}
