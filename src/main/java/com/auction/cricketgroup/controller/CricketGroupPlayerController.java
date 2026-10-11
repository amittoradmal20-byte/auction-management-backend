package com.auction.cricketgroup.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auction.cricketgroup.dto.CricketGroupPlayerAddRequest;
import com.auction.cricketgroup.dto.CricketGroupPlayerResponse;
import com.auction.cricketgroup.service.CricketGroupPlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cricket-groups/{cricketGroupId}/players")
public class CricketGroupPlayerController {

    private final CricketGroupPlayerService cricketGroupPlayerService;
    
	public CricketGroupPlayerController(CricketGroupPlayerService cricketGroupPlayerService) {
		this.cricketGroupPlayerService = cricketGroupPlayerService;
	}

    @PostMapping
    public ResponseEntity<CricketGroupPlayerResponse> addPlayerToGroup(
            @PathVariable UUID cricketGroupId,
            @Valid @RequestBody CricketGroupPlayerAddRequest request) {

        CricketGroupPlayerResponse response =
                cricketGroupPlayerService.addPlayerToGroup(
                        cricketGroupId,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CricketGroupPlayerResponse>> getGroupPlayers(
            @PathVariable UUID cricketGroupId) {

        return ResponseEntity.ok(
                cricketGroupPlayerService.getGroupPlayers(
                        cricketGroupId));
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<CricketGroupPlayerResponse> deactivatePlayer(
            @PathVariable UUID cricketGroupId,
            @PathVariable UUID playerId) {

        return ResponseEntity.ok(
                cricketGroupPlayerService.deactivatePlayerFromGroup(
                        cricketGroupId,
                        playerId));
    }

    @PostMapping("/{playerId}/reactivate")
    public ResponseEntity<CricketGroupPlayerResponse> reactivatePlayer(
            @PathVariable UUID cricketGroupId,
            @PathVariable UUID playerId) {

        return ResponseEntity.ok(
                cricketGroupPlayerService.reactivatePlayerInGroup(
                        cricketGroupId,
                        playerId));
    }
}
