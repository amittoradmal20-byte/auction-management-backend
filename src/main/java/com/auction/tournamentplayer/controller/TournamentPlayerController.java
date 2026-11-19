
package com.auction.tournamentplayer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auction.tournamentplayer.dto.TournamentPlayerCreateRequest;
import com.auction.tournamentplayer.dto.TournamentPlayerResponse;
import com.auction.tournamentplayer.service.TournamentPlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tournament-players")
public class TournamentPlayerController {

    private final TournamentPlayerService tournamentPlayerService;

    public TournamentPlayerController(
            TournamentPlayerService tournamentPlayerService) {

        this.tournamentPlayerService = tournamentPlayerService;
    }

    // =====================================================
    // 1. REGISTER OR RE-REGISTER PLAYER
    // =====================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentPlayerResponse registerPlayer(
            @Valid @RequestBody TournamentPlayerCreateRequest request) {

        return tournamentPlayerService.registerPlayer(request);
    }

    // =====================================================
    // 2. GET PLAYERS BY TOURNAMENT
    // =====================================================

    @GetMapping("/tournament/{tournamentId}")
    public List<TournamentPlayerResponse> getPlayersByTournament(
            @PathVariable UUID tournamentId) {

        return tournamentPlayerService.getPlayersByTournament(tournamentId);
    }

    // =====================================================
    // 3. WITHDRAW PLAYER FROM TOURNAMENT
    // =====================================================

    @PatchMapping("/tournament/{tournamentId}/player/{playerId}/withdraw")
    public TournamentPlayerResponse withdrawPlayer(
            @PathVariable UUID tournamentId,
            @PathVariable UUID playerId) {

        return tournamentPlayerService.withdrawPlayer(
                tournamentId,
                playerId);
    }
}
