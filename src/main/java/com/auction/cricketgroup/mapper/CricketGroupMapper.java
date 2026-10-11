package com.auction.cricketgroup.mapper;

import org.mapstruct.Mapper;

import com.auction.cricketgroup.dto.CricketGroupResponse;
import com.auction.cricketgroup.entity.CricketGroup;

@Mapper(componentModel = "spring")
public interface CricketGroupMapper {

    CricketGroupResponse toResponse(CricketGroup cricketGroup);
}
