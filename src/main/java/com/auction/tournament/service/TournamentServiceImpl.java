package com.auction.tournament.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.tournament.dto.TournamentCreateRequest;
import com.auction.tournament.dto.TournamentResponse;
import com.auction.tournament.dto.TournamentUpdateRequest;
import com.auction.tournament.entity.Tournament;
import com.auction.tournament.enums.TournamentStatus;
import com.auction.tournament.mapper.TournamentMapper;
import com.auction.tournament.repository.TournamentRepository;


@Service
@Transactional
public class TournamentServiceImpl implements TournamentService {

	private final TournamentRepository tournamentRepository;
	private final TournamentMapper tournamentMapper;

	public TournamentServiceImpl(TournamentMapper tournamentMapper, TournamentRepository tournamentRepository) {
		this.tournamentMapper = tournamentMapper;
		this.tournamentRepository = tournamentRepository;
	}

	@Override
	public TournamentResponse createTournament(TournamentCreateRequest request) {

		if (tournamentRepository.existsByNameIgnoreCase(request.getName())) {
			throw new IllegalArgumentException("Tournament with this name already exists");
		}

		if (request.getStartDate() != null && request.getEndDate() != null
				&& request.getEndDate().isBefore(request.getStartDate())) {

			throw new IllegalArgumentException("End date cannot be before start date");
		}

		Tournament tournament = tournamentMapper.toEntity(request);

		tournament.setStatus(TournamentStatus.DRAFT);

		Tournament savedTournament = tournamentRepository.save(tournament);

		return tournamentMapper.toResponse(savedTournament);
	}

	@Override
	@Transactional(readOnly = true)
	public TournamentResponse getTournament(UUID id) {

		Tournament tournament = tournamentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Tournament not found with id: " + id));

		return tournamentMapper.toResponse(tournament);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TournamentResponse> getAllTournaments() {

		return tournamentRepository.findAll().stream().map(tournamentMapper::toResponse).toList();
	}

	@Override
	public TournamentResponse updateTournament(UUID id, TournamentUpdateRequest request) {

		Tournament tournament = tournamentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Tournament not found with id: " + id));

		if (tournament.getStatus() != TournamentStatus.DRAFT) {
			throw new IllegalStateException("Only tournaments in DRAFT status can be updated");
		}

		if (request.getStartDate() != null && request.getEndDate() != null
				&& request.getEndDate().isBefore(request.getStartDate())) {

			throw new IllegalArgumentException("End date cannot be before start date");
		}

		tournamentMapper.updateEntity(request, tournament);

		Tournament updatedTournament = tournamentRepository.save(tournament);

		return tournamentMapper.toResponse(updatedTournament);
	}

	@Override
	public void deleteTournament(UUID id) {

		Tournament tournament = tournamentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Tournament not found with id: " + id));

		if (tournament.getStatus() != TournamentStatus.DRAFT) {
			throw new IllegalStateException("Only DRAFT tournaments can be deleted");
		}

		tournamentRepository.delete(tournament);
	}
}