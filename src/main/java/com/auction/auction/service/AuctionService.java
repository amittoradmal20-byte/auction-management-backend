package com.auction.auction.service;

import java.util.List;
import java.util.UUID;

import com.auction.auction.dto.AuctionCreateRequest;
import com.auction.auction.dto.AuctionResponse;
import com.auction.auction.dto.AuctionUpdateRequest;

public interface AuctionService {

    AuctionResponse createAuction(AuctionCreateRequest request);

    AuctionResponse getAuction(UUID auctionId);

    List<AuctionResponse> getAllAuctions();

    List<AuctionResponse> getAuctionsByTournament(UUID tournamentId);

    AuctionResponse updateAuction(
            UUID auctionId,
            AuctionUpdateRequest request);

    void deleteAuction(UUID auctionId);
    
    AuctionResponse scheduleAuction(UUID auctionId);

    AuctionResponse startAuction(UUID auctionId);

    AuctionResponse pauseAuction(UUID auctionId);

    AuctionResponse resumeAuction(UUID auctionId);

    AuctionResponse completeAuction(UUID auctionId);

    AuctionResponse cancelAuction(UUID auctionId);
}