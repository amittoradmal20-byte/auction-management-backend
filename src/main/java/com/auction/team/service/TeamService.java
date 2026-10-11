package com.auction.team.service;

import java.util.List;
import java.util.UUID;

import com.auction.team.dto.TeamCreateRequest;
import com.auction.team.dto.TeamResponse;
import com.auction.team.dto.TeamUpdateRequest;

public interface TeamService {

    TeamResponse createTeam(TeamCreateRequest request);

    TeamResponse getTeam(UUID id);

    List<TeamResponse> getAllTeams();

    TeamResponse updateTeam(
            UUID id,
            TeamUpdateRequest request);

    void deleteTeam(UUID id);
}
