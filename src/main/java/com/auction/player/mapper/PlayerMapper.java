package com.auction.player.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.auction.player.dto.PlayerCreateRequest;
import com.auction.player.dto.PlayerResponse;
import com.auction.player.dto.PlayerUpdateRequest;
import com.auction.player.entity.Player;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Player toEntity(PlayerCreateRequest request);

    PlayerResponse toResponse(Player player);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(
            PlayerUpdateRequest request,
            @MappingTarget Player player);
}