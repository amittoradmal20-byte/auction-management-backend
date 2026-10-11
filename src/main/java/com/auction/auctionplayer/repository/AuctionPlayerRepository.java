package com.auction.auctionplayer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.auctionplayer.entity.AuctionPlayer;
import com.auction.auctionplayer.enums.AuctionPlayerStatus;

@Repository
public interface AuctionPlayerRepository
        extends JpaRepository<AuctionPlayer, UUID> {

    boolean existsByAuctionIdAndPlayerId(
            UUID auctionId,
            UUID playerId);

    List<AuctionPlayer> findByAuctionId(
            UUID auctionId);

    List<AuctionPlayer> findByAuctionIdAndAuctionStatus(
            UUID auctionId,
            AuctionPlayerStatus auctionStatus);

    boolean existsByAuctionIdAndAuctionStatus(
            UUID auctionId,
            AuctionPlayerStatus auctionStatus);
}