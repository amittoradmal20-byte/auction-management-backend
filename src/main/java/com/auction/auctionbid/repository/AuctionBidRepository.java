package com.auction.auctionbid.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auction.auctionbid.entity.AuctionBid;

@Repository
public interface AuctionBidRepository extends JpaRepository<AuctionBid, UUID> {

    List<AuctionBid> findByAuctionPlayerIdOrderByBidTimeDesc(UUID auctionPlayerId);

    List<AuctionBid> findByTournamentTeamIdOrderByBidTimeDesc(UUID tournamentTeamId);

    Optional<AuctionBid> findFirstByAuctionPlayerIdOrderByBidAmountDescBidTimeAsc(
            UUID auctionPlayerId);
    
    @Query("""
    	    SELECT MAX(ab.bidAmount)
    	    FROM AuctionBid ab
    	    WHERE ab.auctionPlayer.id = :auctionPlayerId
    	    """)
    	BigDecimal findHighestBidAmount(
    	        @Param("auctionPlayerId") UUID auctionPlayerId);
}
