package com.auction.player.service;

import java.util.List;
import java.util.UUID;

import com.auction.player.dto.PlayerCreateRequest;
import com.auction.player.dto.PlayerResponse;
import com.auction.player.dto.PlayerUpdateRequest;
import com.auction.player.enums.PlayerRole;
import com.auction.player.enums.PlayerStatus;

public interface PlayerService {

    PlayerResponse createPlayer(PlayerCreateRequest request);

    PlayerResponse getPlayer(UUID playerId);

    List<PlayerResponse> getAllPlayers();

    List<PlayerResponse> getPlayersByStatus(PlayerStatus status);

    List<PlayerResponse> getPlayersByRole(PlayerRole role);

    List<PlayerResponse> getPlayersByStatusAndRole(
            PlayerStatus status,
            PlayerRole role);

    PlayerResponse updatePlayer(
            UUID playerId,
            PlayerUpdateRequest request);

    void deactivatePlayer(UUID playerId);

    void activatePlayer(UUID playerId);
}