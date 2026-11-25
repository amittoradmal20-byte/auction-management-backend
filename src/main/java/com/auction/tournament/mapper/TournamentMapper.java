
package com.auction.tournament.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.auction.tournament.dto.TournamentCreateRequest;
import com.auction.tournament.dto.TournamentResponse;
import com.auction.tournament.dto.TournamentUpdateRequest;
import com.auction.tournament.entity.Tournament;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    // ==========================================
    // CREATE TOURNAMENT ENTITY
    // ==========================================

    @Mapping(target = "logoUrl", ignore = true)
    Tournament toEntity(TournamentCreateRequest request);

    // ==========================================
    // CONVERT ENTITY TO RESPONSE
    // ==========================================

    @Mapping(source = "logoUrl", target = "logoUrl")
    TournamentResponse toResponse(Tournament tournament);

    // ==========================================
    // UPDATE TOURNAMENT ENTITY
    // ==========================================

    @Mapping(target = "logoUrl", ignore = true)
    void updateEntity(
            TournamentUpdateRequest request,
            @MappingTarget Tournament tournament);
}
