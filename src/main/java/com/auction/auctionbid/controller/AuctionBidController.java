package com.auction.auctionbid.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auction.auctionbid.dto.AuctionBidCreateRequest;
import com.auction.auctionbid.dto.AuctionBidResponse;
import com.auction.auctionbid.service.AuctionBidService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auction-players/{auctionPlayerId}/bids")
public class AuctionBidController {

    private final AuctionBidService auctionBidService;

    public AuctionBidController(
            AuctionBidService auctionBidService) {
        this.auctionBidService = auctionBidService;
    }

    @PostMapping
    public ResponseEntity<AuctionBidResponse> placeBid(
            @PathVariable UUID auctionPlayerId,
            @Valid @RequestBody AuctionBidCreateRequest request) {

        AuctionBidResponse response =
                auctionBidService.placeBid(auctionPlayerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AuctionBidResponse>> getBidHistory(
            @PathVariable UUID auctionPlayerId) {

        List<AuctionBidResponse> response =
                auctionBidService.getBidHistory(auctionPlayerId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/highest")
    public ResponseEntity<AuctionBidResponse> getHighestBid(
            @PathVariable UUID auctionPlayerId) {

        AuctionBidResponse response =
                auctionBidService.getHighestBid(auctionPlayerId);

        return ResponseEntity.ok(response);
    }
}
