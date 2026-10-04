package com.auction.player.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.exception.BusinessException;
import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.player.dto.PlayerCreateRequest;
import com.auction.player.dto.PlayerResponse;
import com.auction.player.dto.PlayerUpdateRequest;
import com.auction.player.entity.Player;
import com.auction.player.enums.PlayerRole;
import com.auction.player.enums.PlayerStatus;
import com.auction.player.mapper.PlayerMapper;
import com.auction.player.repository.PlayerRepository;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    
	public PlayerServiceImpl(PlayerRepository playerRepository, PlayerMapper playerMapper) {
		this.playerRepository = playerRepository;
		this.playerMapper = playerMapper;
	}

    @Override
    public PlayerResponse createPlayer(PlayerCreateRequest request) {

        if (playerRepository.existsByDisplayNameIgnoreCase(request.getDisplayName())) {
            throw new DuplicateResourceException(
                    "Player display name already exists");
        }

        Player player = playerMapper.toEntity(request);

        player.setStatus(PlayerStatus.ACTIVE);

        Player savedPlayer = playerRepository.save(player);

        return playerMapper.toResponse(savedPlayer);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getPlayer(UUID playerId) {

        Player player = findPlayerById(playerId);

        return playerMapper.toResponse(player);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers() {

        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersByStatus(PlayerStatus status) {

        return playerRepository.findByStatus(status)
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersByRole(PlayerRole role) {

        return playerRepository.findByRole(role)
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersByStatusAndRole(
            PlayerStatus status,
            PlayerRole role) {

        return playerRepository.findByStatusAndRole(status, role)
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    @Override
    public PlayerResponse updatePlayer(
            UUID playerId,
            PlayerUpdateRequest request) {

        Player player = findPlayerById(playerId);

        boolean displayNameChanged =
                !player.getDisplayName()
                        .equalsIgnoreCase(request.getDisplayName());

        if (displayNameChanged &&
                playerRepository.existsByDisplayNameIgnoreCase(
                        request.getDisplayName())) {

            throw new DuplicateResourceException(
                    "Player display name already exists");
        }

        playerMapper.updateEntity(request, player);

        Player updatedPlayer = playerRepository.save(player);

        return playerMapper.toResponse(updatedPlayer);
    }

    @Override
    public void deactivatePlayer(UUID playerId) {

        Player player = findPlayerById(playerId);

        if (player.getStatus() == PlayerStatus.INACTIVE) {
            throw new BusinessException(
                    "Player is already inactive");
        }

        player.setStatus(PlayerStatus.INACTIVE);

        playerRepository.save(player);
    }

    @Override
    public void activatePlayer(UUID playerId) {

        Player player = findPlayerById(playerId);

        if (player.getStatus() == PlayerStatus.ACTIVE) {
            throw new BusinessException(
                    "Player is already active");
        }

        player.setStatus(PlayerStatus.ACTIVE);

        playerRepository.save(player);
    }

    private Player findPlayerById(UUID playerId) {

        return playerRepository.findById(playerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Player not found"));
    }
}