package com.auction.auctionplayer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auction.auctionplayer.dto.AuctionPlayerCreateRequest;
import com.auction.auctionplayer.dto.AuctionPlayerResponse;
import com.auction.auctionplayer.dto.AuctionPlayerSaleRequest;
import com.auction.auctionplayer.service.AuctionPlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auction-players")
public class AuctionPlayerController {

    private final AuctionPlayerService auctionPlayerService;

    public AuctionPlayerController(
            AuctionPlayerService auctionPlayerService) {
        this.auctionPlayerService = auctionPlayerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuctionPlayerResponse create(
            @Valid @RequestBody AuctionPlayerCreateRequest request) {

        return auctionPlayerService.create(request);
    }

    @GetMapping("/auction/{auctionId}")
    public List<AuctionPlayerResponse> getPlayersByAuction(
            @PathVariable UUID auctionId) {

        return auctionPlayerService.getPlayersByAuction(auctionId);
    }
    
    @PatchMapping("/{auctionPlayerId}/start-bidding")
    public ResponseEntity<AuctionPlayerResponse> startBidding(
            @PathVariable UUID auctionPlayerId) {

        return ResponseEntity.ok(
                auctionPlayerService.startBidding(auctionPlayerId));
    }

    @PatchMapping("/{auctionPlayerId}/mark-unsold")
    public ResponseEntity<AuctionPlayerResponse> markUnsold(
            @PathVariable UUID auctionPlayerId) {

        return ResponseEntity.ok(
                auctionPlayerService.markUnsold(auctionPlayerId));
    }
    
    @PostMapping("/{auctionPlayerId}/sell")
    public ResponseEntity<AuctionPlayerResponse> sellPlayer(
            @PathVariable UUID auctionPlayerId) {

        return ResponseEntity.ok(
                auctionPlayerService.sellPlayer(auctionPlayerId));
    }
}
