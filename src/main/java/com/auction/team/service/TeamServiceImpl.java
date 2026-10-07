package com.auction.team.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.team.dto.TeamCreateRequest;
import com.auction.team.dto.TeamResponse;
import com.auction.team.dto.TeamUpdateRequest;
import com.auction.team.entity.Team;
import com.auction.team.enums.TeamStatus;
import com.auction.team.mapper.TeamMapper;
import com.auction.team.repository.TeamRepository;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamServiceImpl(
            TeamRepository teamRepository,
            TeamMapper teamMapper) {

        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    @Override
    public TeamResponse createTeam(
            TeamCreateRequest request) {

        String name = request.getName().trim();
        String shortName = request.getShortName().trim();

        // 1. Check duplicate team name
        if (teamRepository.existsByNameIgnoreCase(name)) {

            throw new DuplicateResourceException(
                    "Team name already exists");
        }

        // 2. Check duplicate short name
        if (teamRepository.existsByShortNameIgnoreCase(shortName)) {

            throw new DuplicateResourceException(
                    "Team short name already exists");
        }

        // 3. Create master Team
        Team team = new Team();

        team.setName(name);
        team.setShortName(shortName);

        team.setLogoUrl(
                request.getLogoUrl() != null
                        ? request.getLogoUrl().trim()
                        : null);

        // Server-controlled field
        team.setStatus(TeamStatus.ACTIVE);

        // 4. Save
        Team savedTeam =
                teamRepository.save(team);

        return teamMapper.toResponse(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeam(UUID id) {

        Team team =
                teamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found"));

        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {

        return teamRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Override
    public TeamResponse updateTeam(
            UUID id,
            TeamUpdateRequest request) {

        Team team =
                teamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found"));

        String name = request.getName().trim();
        String shortName = request.getShortName().trim();

        // 1. Check duplicate name only if changed
        if (!team.getName().equalsIgnoreCase(name)
                && teamRepository.existsByNameIgnoreCase(name)) {

            throw new DuplicateResourceException(
                    "Team name already exists");
        }

        // 2. Check duplicate short name only if changed
        if (!team.getShortName().equalsIgnoreCase(shortName)
                && teamRepository.existsByShortNameIgnoreCase(shortName)) {

            throw new DuplicateResourceException(
                    "Team short name already exists");
        }

        // 3. Update editable fields
        teamMapper.updateEntity(request, team);

        // 4. Save
        Team updatedTeam =
                teamRepository.save(team);

        return teamMapper.toResponse(updatedTeam);
    }

    @Override
    public void deleteTeam(UUID id) {

        Team team =
                teamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found"));

        teamRepository.delete(team);
    }
}
