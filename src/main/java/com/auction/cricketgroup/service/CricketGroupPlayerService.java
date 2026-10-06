package com.auction.cricketgroup.service;

import java.util.List;
import java.util.UUID;

import com.auction.cricketgroup.dto.CricketGroupPlayerAddRequest;
import com.auction.cricketgroup.dto.CricketGroupPlayerResponse;

public interface CricketGroupPlayerService {

    CricketGroupPlayerResponse addPlayerToGroup(
            UUID cricketGroupId,
            CricketGroupPlayerAddRequest request);

    List<CricketGroupPlayerResponse> getGroupPlayers(
            UUID cricketGroupId);

    CricketGroupPlayerResponse deactivatePlayerFromGroup(
            UUID cricketGroupId,
            UUID playerId);

    CricketGroupPlayerResponse reactivatePlayerInGroup(
            UUID cricketGroupId,
            UUID playerId);
}
