package com.auction.tournamentteam.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.entity.UserAccount;
import com.auction.exception.BusinessException;
import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.repository.UserRepository;
import com.auction.team.entity.Team;
import com.auction.team.repository.TeamRepository;
import com.auction.tournament.entity.Tournament;
import com.auction.tournament.enums.TournamentStatus;
import com.auction.tournament.repository.TournamentRepository;
import com.auction.tournamentteam.dto.TournamentTeamCreateRequest;
import com.auction.tournamentteam.dto.TournamentTeamResponse;
import com.auction.tournamentteam.dto.TournamentTeamUpdateRequest;
import com.auction.tournamentteam.entity.TournamentTeam;
import com.auction.tournamentteam.enums.TournamentTeamStatus;
import com.auction.tournamentteam.mapper.TournamentTeamMapper;
import com.auction.tournamentteam.repository.TournamentTeamRepository;

@Service
@Transactional
public class TournamentTeamServiceImpl
        implements TournamentTeamService {

    private final TournamentTeamRepository tournamentTeamRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TournamentTeamMapper tournamentTeamMapper;

    public TournamentTeamServiceImpl(
            TournamentTeamRepository tournamentTeamRepository,
            TournamentRepository tournamentRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            TournamentTeamMapper tournamentTeamMapper) {

        this.tournamentTeamRepository = tournamentTeamRepository;
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.tournamentTeamMapper = tournamentTeamMapper;
    }

    @Override
    public TournamentTeamResponse createTournamentTeam(
            TournamentTeamCreateRequest request) {

        /*
         * 1. Load tournament
         */
        Tournament tournament =
                tournamentRepository.findById(
                        request.getTournamentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tournament not found"));

        /*
         * 2. Tournament must be in DRAFT
         */
        if (tournament.getStatus()
                != TournamentStatus.DRAFT) {

            throw new BusinessException(
                    "Teams can only be added while tournament is in DRAFT");
        }

        /*
         * 3. Check maximum number of teams
         */
        long currentTeamCount =
                tournamentTeamRepository
                        .findByTournamentId(
                                tournament.getId())
                        .size();

        if (currentTeamCount >= tournament.getMaxTeams()) {

            throw new BusinessException(
                    "Tournament has reached the maximum number of teams");
        }

        /*
         * 4. Load master team
         */
        Team team =
                teamRepository.findById(
                        request.getTeamId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team not found"));

        /*
         * 5. Master team must be active
         */
        if (team.getStatus() == null
                || !team.getStatus().name().equals("ACTIVE")) {

            throw new BusinessException(
                    "Only active teams can be added to a tournament");
        }

        /*
         * 6. Same team cannot participate twice
         *    in the same tournament
         */
        if (tournamentTeamRepository
                .existsByTournamentIdAndTeamId(
                        tournament.getId(),
                        team.getId())) {

            throw new DuplicateResourceException(
                    "Team is already registered in this tournament");
        }

        /*
         * 7. Load owner
         */
        UserAccount owner =
                userRepository.findById(
                        request.getOwnerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Owner not found"));

        /*
         * 8. Same owner cannot own multiple teams
         *    in the same tournament
         */
        if (tournamentTeamRepository
                .existsByTournamentIdAndOwnerId(
                        tournament.getId(),
                        owner.getId())) {

            throw new DuplicateResourceException(
                    "Owner is already assigned to a team in this tournament");
        }

        /*
         * 9. Create TournamentTeam
         */
        TournamentTeam tournamentTeam =
                new TournamentTeam();

        tournamentTeam.setTournament(tournament);
        tournamentTeam.setTeam(team);
        tournamentTeam.setOwner(owner);

        tournamentTeam.setInitialBudget(
                request.getInitialBudget());

        tournamentTeam.setRemainingBudget(
                request.getInitialBudget());

        tournamentTeam.setStatus(
                TournamentTeamStatus.ACTIVE);

        /*
         * 10. Save
         */
        TournamentTeam savedTournamentTeam =
                tournamentTeamRepository.save(
                        tournamentTeam);

        return tournamentTeamMapper.toResponse(
                savedTournamentTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentTeamResponse getTournamentTeam(
            UUID id) {

        TournamentTeam tournamentTeam =
                tournamentTeamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tournament team not found"));

        return tournamentTeamMapper.toResponse(
                tournamentTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentTeamResponse> getTeamsByTournament(
            UUID tournamentId) {

        if (!tournamentRepository.existsById(tournamentId)) {

            throw new ResourceNotFoundException(
                    "Tournament not found");
        }

        return tournamentTeamRepository
                .findByTournamentIdOrderByTeam_NameAsc(
                        tournamentId)
                .stream()
                .map(tournamentTeamMapper::toResponse)
                .toList();
    }

    @Override
    public TournamentTeamResponse updateTournamentTeam(
            UUID id,
            TournamentTeamUpdateRequest request) {

        TournamentTeam tournamentTeam =
                tournamentTeamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tournament team not found"));

        Tournament tournament =
                tournamentTeam.getTournament();

        /*
         * Status changes are allowed only while
         * tournament is in DRAFT.
         */
        if (tournament.getStatus()
                != TournamentStatus.DRAFT) {

            throw new BusinessException(
                    "Tournament team can only be updated while tournament is in DRAFT");
        }

        tournamentTeam.setStatus(
                request.getStatus());

        TournamentTeam updatedTournamentTeam =
                tournamentTeamRepository.save(
                        tournamentTeam);

        return tournamentTeamMapper.toResponse(
                updatedTournamentTeam);
    }

    @Override
    public void deleteTournamentTeam(UUID id) {

        TournamentTeam tournamentTeam =
                tournamentTeamRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tournament team not found"));

        Tournament tournament =
                tournamentTeam.getTournament();

        /*
         * Teams can only be removed while
         * tournament is in DRAFT.
         */
        if (tournament.getStatus()
                != TournamentStatus.DRAFT) {

            throw new BusinessException(
                    "Team can only be removed while tournament is in DRAFT");
        }

        tournamentTeamRepository.delete(
                tournamentTeam);
    }
}