package com.auction.player.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auction.player.dto.PlayerCreateRequest;
import com.auction.player.dto.PlayerResponse;
import com.auction.player.dto.PlayerUpdateRequest;
import com.auction.player.enums.PlayerRole;
import com.auction.player.enums.PlayerStatus;
import com.auction.player.service.PlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final PlayerService playerService;
    
	public PlayerController(PlayerService playerService) {
		this.playerService = playerService;
	}


    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(
            @Valid @RequestBody PlayerCreateRequest request) {

        PlayerResponse response = playerService.createPlayer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> getPlayer(
            @PathVariable UUID playerId) {

        return ResponseEntity.ok(
                playerService.getPlayer(playerId));
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getPlayers(
            @RequestParam(required = false) PlayerStatus status,
            @RequestParam(required = false) PlayerRole role) {

        if (status != null && role != null) {

            return ResponseEntity.ok(
                    playerService.getPlayersByStatusAndRole(
                            status,
                            role));
        }

        if (status != null) {

            return ResponseEntity.ok(
                    playerService.getPlayersByStatus(status));
        }

        if (role != null) {

            return ResponseEntity.ok(
                    playerService.getPlayersByRole(role));
        }

        return ResponseEntity.ok(
                playerService.getAllPlayers());
    }

    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable UUID playerId,
            @Valid @RequestBody PlayerUpdateRequest request) {

        return ResponseEntity.ok(
                playerService.updatePlayer(
                        playerId,
                        request));
    }

    @PatchMapping("/{playerId}/deactivate")
    public ResponseEntity<Void> deactivatePlayer(
            @PathVariable UUID playerId) {

        playerService.deactivatePlayer(playerId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{playerId}/activate")
    public ResponseEntity<Void> activatePlayer(
            @PathVariable UUID playerId) {

        playerService.activatePlayer(playerId);

        return ResponseEntity.noContent().build();
    }
}