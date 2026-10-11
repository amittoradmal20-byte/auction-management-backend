package com.auction.team.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.auction.team.dto.TeamResponse;
import com.auction.team.dto.TeamUpdateRequest;
import com.auction.team.entity.Team;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    TeamResponse toResponse(Team team);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(
            TeamUpdateRequest request,
            @MappingTarget Team team);
}
