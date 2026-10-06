package com.auction.auction.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.auction.entity.Auction;
import com.auction.auction.enums.AuctionStatus;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    List<Auction> findByTournamentId(UUID tournamentId);

    boolean existsByTournamentIdAndStatus(
            UUID tournamentId,
            AuctionStatus status);

    boolean existsByTournamentIdAndStatusIn(
            UUID tournamentId,
            List<AuctionStatus> statuses);
}