package com.auction.tournamentplayer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.auction.tournamentplayer.dto.TournamentPlayerResponse;
import com.auction.tournamentplayer.entity.TournamentPlayer;

@Mapper(componentModel = "spring")
public interface TournamentPlayerMapper {

    @Mapping(source = "tournament.id", target = "tournamentId")
    @Mapping(source = "player.id", target = "playerId")
    TournamentPlayerResponse toResponse(
            TournamentPlayer tournamentPlayer);
}
