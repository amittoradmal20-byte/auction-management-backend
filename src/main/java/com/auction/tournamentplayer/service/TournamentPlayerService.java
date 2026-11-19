
package com.auction.tournamentplayer.service;

import java.util.List;
import java.util.UUID;

import com.auction.tournamentplayer.dto.TournamentPlayerCreateRequest;
import com.auction.tournamentplayer.dto.TournamentPlayerResponse;

public interface TournamentPlayerService {

    // Register a player or re-register a withdrawn player
    TournamentPlayerResponse registerPlayer(
            TournamentPlayerCreateRequest request);

    // Retrieve all players registered for a tournament
    List<TournamentPlayerResponse> getPlayersByTournament(
            UUID tournamentId);

    // Withdraw an existing player from a tournament
    TournamentPlayerResponse withdrawPlayer(
            UUID tournamentId,
            UUID playerId);
}
