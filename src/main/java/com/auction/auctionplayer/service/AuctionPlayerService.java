package com.auction.auctionplayer.service;

import com.auction.auctionplayer.dto.AuctionPlayerCreateRequest;
import com.auction.auctionplayer.dto.AuctionPlayerResponse;

public interface AuctionPlayerService {

    AuctionPlayerResponse create(AuctionPlayerCreateRequest request);

}
