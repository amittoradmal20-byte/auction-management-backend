package com.auction.cricketgroup.service;

import java.util.List;
import java.util.UUID;

import com.auction.cricketgroup.dto.CricketGroupCreateRequest;
import com.auction.cricketgroup.dto.CricketGroupResponse;

public interface CricketGroupService {

    CricketGroupResponse createGroup(
            CricketGroupCreateRequest request);

    CricketGroupResponse getGroupById(
            UUID groupId);

    List<CricketGroupResponse> getAllGroups();
}
