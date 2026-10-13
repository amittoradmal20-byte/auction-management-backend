package com.auction.auctionbid.service;

import java.util.List;
import java.util.UUID;

import com.auction.auctionbid.dto.AuctionBidCreateRequest;
import com.auction.auctionbid.dto.AuctionBidResponse;

public interface AuctionBidService {

    AuctionBidResponse placeBid(
            UUID auctionPlayerId,
            AuctionBidCreateRequest request);

    List<AuctionBidResponse> getBidHistory(
            UUID auctionPlayerId);

    AuctionBidResponse getHighestBid(
            UUID auctionPlayerId);
}
