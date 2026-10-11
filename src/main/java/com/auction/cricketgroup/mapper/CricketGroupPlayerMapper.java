package com.auction.cricketgroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.auction.cricketgroup.dto.CricketGroupPlayerResponse;
import com.auction.cricketgroup.entity.CricketGroupPlayer;

@Mapper(componentModel = "spring")
public interface CricketGroupPlayerMapper {

    @Mapping(
        source = "cricketGroup.id",
        target = "cricketGroupId"
    )
    @Mapping(
        source = "player.id",
        target = "playerId"
    )
    @Mapping(
        source = "player.displayName",
        target = "playerDisplayName"
    )
    CricketGroupPlayerResponse toResponse(
            CricketGroupPlayer cricketGroupPlayer);
}
