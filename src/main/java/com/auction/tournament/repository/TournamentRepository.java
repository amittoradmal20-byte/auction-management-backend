package com.auction.tournament.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.tournament.entity.Tournament;

public interface TournamentRepository extends JpaRepository<Tournament, UUID> {

    Optional<Tournament> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}