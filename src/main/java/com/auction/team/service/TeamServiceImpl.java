package com.auction.team.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.entity.UserAccount;
import com.auction.exception.BusinessException;
import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.repository.UserRepository;
import com.auction.team.dto.TeamCreateRequest;
import com.auction.team.dto.TeamResponse;
import com.auction.team.dto.TeamUpdateRequest;
import com.auction.team.entity.Team;
import com.auction.team.enums.TeamStatus;
import com.auction.team.mapper.TeamMapper;
import com.auction.team.repository.TeamRepository;
import com.auction.tournament.entity.Tournament;
import com.auction.tournament.enums.TournamentStatus;
import com.auction.tournament.mapper.TournamentMapper;
import com.auction.tournament.repository.TournamentRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final TeamMapper teamMapper;
    

	public TeamServiceImpl(TeamRepository teamRepository, TournamentRepository tournamentRepository,
			UserRepository userRepository, TeamMapper teamMapper) {
		this.teamRepository = teamRepository;
		this.tournamentRepository = tournamentRepository;
		this.userRepository = userRepository;
		this.teamMapper = teamMapper;
	}

    @Override
    public TeamResponse createTeam(TeamCreateRequest request) {

        // 1. Load tournament
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tournament not found"));

        // 2. Tournament must be in DRAFT
        if (tournament.getStatus() != TournamentStatus.DRAFT) {
            throw new BusinessException(
                    "Teams can only be created while tournament is in DRAFT status");
        }

        // 3. Check maximum teams
        long existingTeamCount =
                teamRepository.findByTournamentId(tournament.getId()).size();

        if (existingTeamCount >= tournament.getMaxTeams()) {
            throw new BusinessException(
                    "Tournament has reached the maximum number of teams");
        }

        // 4. Check duplicate team name
        if (teamRepository.existsByTournamentIdAndNameIgnoreCase(
                tournament.getId(),
                request.getName())) {

        	throw new DuplicateResourceException(
        		    "Team name already exists in this tournament"
        		);
        }

        // 5. Check duplicate short name
        if (teamRepository.existsByTournamentIdAndShortNameIgnoreCase(
                tournament.getId(),
                request.getShortName())) {

        	throw new DuplicateResourceException(
        		    "Team short name already exists in this tournament"
        		);
        }

        // 6. Load owner
        UserAccount owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Team owner not found"));

        // 7. One owner -> one team per tournament
        if (teamRepository.existsByOwnerIdAndTournamentId(
                owner.getId(),
                tournament.getId())) {

            throw new BusinessException(
                    "User already owns a team in this tournament");
        }

        // 8. Create Team
        Team team = new Team();

        team.setTournament(tournament);
        team.setOwner(owner);
        team.setName(request.getName());
        team.setShortName(request.getShortName());
        team.setLogoUrl(request.getLogoUrl());

        team.setInitialBudget(request.getInitialBudget());

        // Server controlled fields
        team.setRemainingBudget(request.getInitialBudget());
        team.setStatus(TeamStatus.ACTIVE);

        // 9. Save
        Team savedTeam = teamRepository.save(team);

        return teamMapper.toResponse(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeam(UUID id) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Team not found"));

        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsByTournament(UUID tournamentId) {

        // Make sure tournament exists
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new IllegalArgumentException("Tournament not found");
        }

        return teamRepository.findByTournamentId(tournamentId)
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Override
    public TeamResponse updateTeam(
            UUID id,
            TeamUpdateRequest request) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Team not found"));

        Tournament tournament = team.getTournament();

        // Teams can only be modified during DRAFT
        if (tournament.getStatus() != TournamentStatus.DRAFT) {
            throw new IllegalStateException(
                    "Team can only be updated while tournament is in DRAFT status");
        }

        // Check duplicate name only if changed
        if (!team.getName().equalsIgnoreCase(request.getName())
                && teamRepository.existsByTournamentIdAndNameIgnoreCase(
                        tournament.getId(),
                        request.getName())) {

            throw new IllegalArgumentException(
                    "Team name already exists in this tournament");
        }

        // Check duplicate short name only if changed
        if (!team.getShortName().equalsIgnoreCase(request.getShortName())
                && teamRepository.existsByTournamentIdAndShortNameIgnoreCase(
                        tournament.getId(),
                        request.getShortName())) {

            throw new IllegalArgumentException(
                    "Team short name already exists in this tournament");
        }

        // Update editable fields
        teamMapper.updateEntity(request, team);

        /*
         * Important:
         *
         * remainingBudget is NOT changed here.
         *
         * Example:
         *
         * Initial Budget   = 50,000
         * Remaining Budget = 35,000
         *
         * Updating team details should not reset the auction balance.
         */

        Team updatedTeam = teamRepository.save(team);

        return teamMapper.toResponse(updatedTeam);
    }

    @Override
    public void deleteTeam(UUID id) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Team not found"));

        Tournament tournament = team.getTournament();

        // Teams can only be deleted during DRAFT
        if (tournament.getStatus() != TournamentStatus.DRAFT) {
            throw new BusinessException(
                    "Team can only be deleted while tournament is in DRAFT status");
        }

        teamRepository.delete(team);
    }
}