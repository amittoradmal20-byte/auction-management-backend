package com.auction.auctionplayer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.auction.auctionplayer.dto.AuctionPlayerResponse;
import com.auction.auctionplayer.entity.AuctionPlayer;


@Mapper(componentModel = "spring")
public interface AuctionPlayerMapper {

    @Mapping(source = "auction.id", target = "auctionId")
    @Mapping(source = "player.id", target = "playerId")
    @Mapping(
        source = "auctionStatus",
        target = "status"
    )
    @Mapping(
        source = "soldToTournamentTeam.id",
        target = "soldToTournamentTeamId"
    )
    AuctionPlayerResponse toResponse(
            AuctionPlayer auctionPlayer);
}
