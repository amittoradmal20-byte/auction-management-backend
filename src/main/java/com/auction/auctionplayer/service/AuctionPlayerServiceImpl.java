package com.auction.auctionplayer.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.auctionplayer.dto.AuctionPlayerCreateRequest;
import com.auction.auctionplayer.dto.AuctionPlayerResponse;
import com.auction.auctionplayer.mapper.AuctionPlayerMapper;
import com.auction.auctionplayer.repository.AuctionPlayerRepository;
import com.auction.exception.ResourceNotFoundException;
import com.auction.auction.entity.Auction;
import com.auction.auction.repository.AuctionRepository;
import com.auction.player.repository.PlayerRepository;

@Service
public class AuctionPlayerServiceImpl implements AuctionPlayerService{

    private final AuctionPlayerRepository auctionPlayerRepository;
    private final AuctionRepository auctionRepository;
    private final PlayerRepository playerRepository;
    private final AuctionPlayerMapper auctionPlayerMapper;
    
	public AuctionPlayerServiceImpl(AuctionPlayerRepository auctionPlayerRepository, AuctionRepository auctionRepository,
			PlayerRepository playerRepository, 	AuctionPlayerMapper auctionPlayerMapper) {
		this.auctionPlayerRepository = auctionPlayerRepository;
		this.auctionRepository = auctionRepository;
		this.playerRepository = playerRepository;
		this.auctionPlayerMapper = auctionPlayerMapper;
	}

	@Override
	@Transactional
	public AuctionPlayerResponse create(
	        AuctionPlayerCreateRequest request) {

	    // 1. Validate auction exists
	    Auction auction = auctionRepository.findById(request.getAuctionId())
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Auction not found: " + request.getAuctionId()));

	    return null;
	}
}
