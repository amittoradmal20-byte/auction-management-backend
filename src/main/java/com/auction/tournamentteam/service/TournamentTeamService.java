package com.auction.tournamentteam.service;

import java.util.List;
import java.util.UUID;

import com.auction.tournamentteam.dto.TournamentTeamCreateRequest;
import com.auction.tournamentteam.dto.TournamentTeamResponse;
import com.auction.tournamentteam.dto.TournamentTeamUpdateRequest;

public interface TournamentTeamService {

    TournamentTeamResponse createTournamentTeam(
            TournamentTeamCreateRequest request);

    TournamentTeamResponse getTournamentTeam(UUID id);

    List<TournamentTeamResponse> getTeamsByTournament(
            UUID tournamentId);

    TournamentTeamResponse updateTournamentTeam(
            UUID id,
            TournamentTeamUpdateRequest request);

    void deleteTournamentTeam(UUID id);
}