package com.auction.auction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.auction.auction.dto.AuctionCreateRequest;
import com.auction.auction.dto.AuctionResponse;
import com.auction.auction.dto.AuctionUpdateRequest;
import com.auction.auction.entity.Auction;

@Mapper(componentModel = "spring")
public interface AuctionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "status", ignore = true)
    Auction toEntity(AuctionCreateRequest request);

    @Mapping(source = "tournament.id", target = "tournamentId")
    AuctionResponse toResponse(Auction auction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(
            AuctionUpdateRequest request,
            @MappingTarget Auction auction);
}