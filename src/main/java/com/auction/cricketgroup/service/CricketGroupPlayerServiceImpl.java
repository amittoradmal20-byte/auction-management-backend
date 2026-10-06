package com.auction.cricketgroup.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.cricketgroup.dto.CricketGroupPlayerAddRequest;
import com.auction.cricketgroup.dto.CricketGroupPlayerResponse;
import com.auction.cricketgroup.entity.CricketGroup;
import com.auction.cricketgroup.entity.CricketGroupPlayer;
import com.auction.cricketgroup.enums.CricketGroupStatus;
import com.auction.cricketgroup.mapper.CricketGroupPlayerMapper;
import com.auction.cricketgroup.repository.CricketGroupPlayerRepository;
import com.auction.cricketgroup.repository.CricketGroupRepository;
import com.auction.exception.BusinessException;
import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.player.entity.Player;
import com.auction.player.repository.PlayerRepository;

@Service
@Transactional
public class CricketGroupPlayerServiceImpl
        implements CricketGroupPlayerService {

    private final CricketGroupRepository cricketGroupRepository;

    private final CricketGroupPlayerRepository cricketGroupPlayerRepository;

    private final PlayerRepository playerRepository;

    private final CricketGroupPlayerMapper cricketGroupPlayerMapper;

    public CricketGroupPlayerServiceImpl(
            CricketGroupRepository cricketGroupRepository,
            CricketGroupPlayerRepository cricketGroupPlayerRepository,
            PlayerRepository playerRepository,
            CricketGroupPlayerMapper cricketGroupPlayerMapper) {

        this.cricketGroupRepository = cricketGroupRepository;
        this.cricketGroupPlayerRepository =
                cricketGroupPlayerRepository;
        this.playerRepository = playerRepository;
        this.cricketGroupPlayerMapper =
                cricketGroupPlayerMapper;
    }

    @Override
    public CricketGroupPlayerResponse addPlayerToGroup(
            UUID cricketGroupId,
            CricketGroupPlayerAddRequest request) {

        CricketGroup cricketGroup =
                cricketGroupRepository.findById(cricketGroupId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cricket group not found"));

        if (cricketGroup.getStatus() != CricketGroupStatus.ACTIVE) {
            throw new BusinessException(
                    "Cannot add player to an inactive cricket group");
        }

        Player player =
                playerRepository.findById(request.getPlayerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Player not found"));

        CricketGroupPlayer membership =
                cricketGroupPlayerRepository
                        .findByCricketGroupId(cricketGroupId)
                        .stream()
                        .filter(existing ->
                                existing.getPlayer()
                                        .getId()
                                        .equals(player.getId()))
                        .findFirst()
                        .orElse(null);

        if (membership != null) {

            if (membership.isActive()) {
                throw new DuplicateResourceException(
                        "Player is already an active member of this cricket group");
            }

            membership.setActive(true);

            CricketGroupPlayer reactivatedMembership =
                    cricketGroupPlayerRepository.save(membership);

            return cricketGroupPlayerMapper.toResponse(
                    reactivatedMembership);
        }

        CricketGroupPlayer newMembership =
                new CricketGroupPlayer();

        newMembership.setCricketGroup(cricketGroup);
        newMembership.setPlayer(player);
        newMembership.setActive(true);

        CricketGroupPlayer savedMembership =
                cricketGroupPlayerRepository.save(newMembership);

        return cricketGroupPlayerMapper.toResponse(
                savedMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CricketGroupPlayerResponse> getGroupPlayers(
            UUID cricketGroupId) {

        cricketGroupRepository.findById(cricketGroupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cricket group not found"));

        return cricketGroupPlayerRepository
                .findByCricketGroupIdAndActive(
                        cricketGroupId,
                        true)
                .stream()
                .map(cricketGroupPlayerMapper::toResponse)
                .toList();
    }

    @Override
    public CricketGroupPlayerResponse deactivatePlayerFromGroup(
            UUID cricketGroupId,
            UUID playerId) {

        CricketGroupPlayer membership =
                cricketGroupPlayerRepository
                        .findByCricketGroupId(cricketGroupId)
                        .stream()
                        .filter(existing ->
                                existing.getPlayer()
                                        .getId()
                                        .equals(playerId))
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Player is not a member of this cricket group"));

        if (!membership.isActive()) {
            throw new BusinessException(
                    "Player is already inactive in this cricket group");
        }

        membership.setActive(false);

        CricketGroupPlayer savedMembership =
                cricketGroupPlayerRepository.save(membership);

        return cricketGroupPlayerMapper.toResponse(
                savedMembership);
    }

    @Override
    public CricketGroupPlayerResponse reactivatePlayerInGroup(
            UUID cricketGroupId,
            UUID playerId) {

        CricketGroupPlayer membership =
                cricketGroupPlayerRepository
                        .findByCricketGroupId(cricketGroupId)
                        .stream()
                        .filter(existing ->
                                existing.getPlayer()
                                        .getId()
                                        .equals(playerId))
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Player is not a member of this cricket group"));

        if (membership.isActive()) {
            throw new BusinessException(
                    "Player is already active in this cricket group");
        }

        CricketGroup cricketGroup =
                cricketGroupRepository.findById(cricketGroupId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cricket group not found"));

        if (cricketGroup.getStatus() != CricketGroupStatus.ACTIVE) {
            throw new BusinessException(
                    "Cannot reactivate player in an inactive cricket group");
        }

        membership.setActive(true);

        CricketGroupPlayer savedMembership =
                cricketGroupPlayerRepository.save(membership);

        return cricketGroupPlayerMapper.toResponse(
                savedMembership);
    }
}
