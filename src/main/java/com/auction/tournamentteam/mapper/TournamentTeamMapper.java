package com.auction.tournamentteam.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.auction.tournamentteam.dto.TournamentTeamResponse;
import com.auction.tournamentteam.entity.TournamentTeam;

@Mapper(componentModel = "spring")
public interface TournamentTeamMapper {

    @Mapping(source = "tournament.id", target = "tournamentId")
    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "owner.id", target = "ownerId")
    TournamentTeamResponse toResponse(
            TournamentTeam tournamentTeam);
}