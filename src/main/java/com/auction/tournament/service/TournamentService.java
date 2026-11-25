
package com.auction.tournament.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.auction.tournament.dto.TournamentCreateRequest;
import com.auction.tournament.dto.TournamentResponse;
import com.auction.tournament.dto.TournamentUpdateRequest;

public interface TournamentService {

    // ==========================================
    // TOURNAMENT CRUD OPERATIONS
    // ==========================================

    TournamentResponse createTournament(
            TournamentCreateRequest request);

    TournamentResponse getTournament(UUID id);

    List<TournamentResponse> getAllTournaments();

    TournamentResponse updateTournament(
            UUID id,
            TournamentUpdateRequest request);

    void deleteTournament(UUID id);

    // ==========================================
    // TOURNAMENT REGISTRATION OPERATIONS
    // ==========================================

    TournamentResponse openRegistration(UUID tournamentId);

    TournamentResponse closeRegistration(UUID tournamentId);

    // ==========================================
    // TOURNAMENT AUCTION PREPARATION
    // ==========================================

    TournamentResponse prepareAuction(UUID tournamentId);

    // ==========================================
    // TOURNAMENT LOGO MANAGEMENT
    // ==========================================

    TournamentResponse uploadLogo(
            UUID tournamentId,
            MultipartFile file);

    TournamentResponse deleteLogo(
            UUID tournamentId);
}
