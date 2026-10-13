package com.auction.auctionplayer.service;

import java.util.List;
import java.util.UUID;

import com.auction.auctionplayer.dto.AuctionPlayerCreateRequest;
import com.auction.auctionplayer.dto.AuctionPlayerResponse;
import com.auction.auctionplayer.dto.AuctionPlayerSaleRequest;

public interface AuctionPlayerService {

    AuctionPlayerResponse create(AuctionPlayerCreateRequest request);

    List<AuctionPlayerResponse> getPlayersByAuction(UUID auctionId);

    AuctionPlayerResponse startBidding(UUID auctionPlayerId);

    AuctionPlayerResponse markUnsold(UUID auctionPlayerId);
    
    AuctionPlayerResponse sellPlayer(UUID auctionPlayerId);
}
