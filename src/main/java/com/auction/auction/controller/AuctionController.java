package com.auction.auction.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auction.auction.dto.AuctionCreateRequest;
import com.auction.auction.dto.AuctionResponse;
import com.auction.auction.dto.AuctionUpdateRequest;
import com.auction.auction.service.AuctionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    
	public AuctionController(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(
            @Valid @RequestBody AuctionCreateRequest request) {

        AuctionResponse response =
                auctionService.createAuction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionResponse> getAuction(
            @PathVariable UUID auctionId) {

        return ResponseEntity.ok(
                auctionService.getAuction(auctionId));
    }

    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAuctions(
            @RequestParam(required = false) UUID tournamentId) {

        if (tournamentId != null) {
            return ResponseEntity.ok(
                    auctionService.getAuctionsByTournament(
                            tournamentId));
        }

        return ResponseEntity.ok(
                auctionService.getAllAuctions());
    }

    @PutMapping("/{auctionId}")
    public ResponseEntity<AuctionResponse> updateAuction(
            @PathVariable UUID auctionId,
            @Valid @RequestBody AuctionUpdateRequest request) {

        return ResponseEntity.ok(
                auctionService.updateAuction(
                        auctionId,
                        request));
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Void> deleteAuction(
            @PathVariable UUID auctionId) {

        auctionService.deleteAuction(auctionId);

        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{auctionId}/schedule")
    public ResponseEntity<AuctionResponse> scheduleAuction(
            @PathVariable UUID auctionId) {

        return ResponseEntity.ok(
                auctionService.scheduleAuction(auctionId));
    }
    
    @PostMapping("/{auctionId}/start")
    public ResponseEntity<AuctionResponse> startAuction(
            @PathVariable UUID auctionId) {

        return ResponseEntity.ok(
                auctionService.startAuction(auctionId));
    }
    
    @PostMapping("/{auctionId}/pause")
    public ResponseEntity<AuctionResponse> pauseAuction(
            @PathVariable UUID auctionId) {

        return ResponseEntity.ok(
                auctionService.pauseAuction(auctionId));
    }
    
    @PostMapping("/{auctionId}/resume")
    public ResponseEntity<AuctionResponse> resumeAuction(
            @PathVariable UUID auctionId) {

        return ResponseEntity.ok(
                auctionService.resumeAuction(auctionId));
    }
    
    @PostMapping("/{auctionId}/complete")
    public ResponseEntity<AuctionResponse> completeAuction(
            @PathVariable UUID auctionId) {

        return ResponseEntity.ok(
                auctionService.completeAuction(auctionId));
    }
    
    @PostMapping("/{auctionId}/cancel")
    public ResponseEntity<AuctionResponse> cancelAuction(
            @PathVariable UUID auctionId) {

        return ResponseEntity.ok(
                auctionService.cancelAuction(auctionId));
    }
    
}