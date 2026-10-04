package com.auction.tournament.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.auction.tournament.dto.TournamentCreateRequest;
import com.auction.tournament.dto.TournamentResponse;
import com.auction.tournament.dto.TournamentUpdateRequest;
import com.auction.tournament.entity.Tournament;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    Tournament toEntity(TournamentCreateRequest request);

    TournamentResponse toResponse(Tournament tournament);

    void updateEntity(
            TournamentUpdateRequest request,
            @MappingTarget Tournament tournament);
}