package com.auction.tournament.service;

import java.util.List;
import java.util.UUID;

import com.auction.tournament.dto.TournamentCreateRequest;
import com.auction.tournament.dto.TournamentResponse;
import com.auction.tournament.dto.TournamentUpdateRequest;

public interface TournamentService {

	TournamentResponse createTournament(TournamentCreateRequest request);

	TournamentResponse getTournament(UUID id);

	List<TournamentResponse> getAllTournaments();

	TournamentResponse updateTournament(UUID id, TournamentUpdateRequest request);

	void deleteTournament(UUID id);
}