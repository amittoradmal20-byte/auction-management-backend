package com.auction.tournamentplayer.service;

import java.util.List;
import java.util.UUID;

import com.auction.tournamentplayer.dto.TournamentPlayerCreateRequest;
import com.auction.tournamentplayer.dto.TournamentPlayerResponse;


public interface TournamentPlayerService {

    TournamentPlayerResponse registerPlayer(
            TournamentPlayerCreateRequest request);

    List<TournamentPlayerResponse> getPlayersByTournament(
            UUID tournamentId);
}
