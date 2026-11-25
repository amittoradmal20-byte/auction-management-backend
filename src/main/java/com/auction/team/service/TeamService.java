
package com.auction.team.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.auction.team.dto.TeamCreateRequest;
import com.auction.team.dto.TeamResponse;
import com.auction.team.dto.TeamUpdateRequest;

public interface TeamService {

    // ==========================================
    // TEAM CRUD OPERATIONS
    // ==========================================

    TeamResponse createTeam(TeamCreateRequest request);

    TeamResponse getTeam(UUID id);

    List<TeamResponse> getAllTeams();

    TeamResponse updateTeam(
            UUID id,
            TeamUpdateRequest request);

    void deleteTeam(UUID id);

    // ==========================================
    // TEAM LOGO MANAGEMENT
    // ==========================================

    TeamResponse uploadLogo(
            UUID teamId,
            MultipartFile file);

    TeamResponse deleteLogo(
            UUID teamId);
}
