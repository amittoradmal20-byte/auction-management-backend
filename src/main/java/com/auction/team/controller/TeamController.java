
package com.auction.team.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auction.team.dto.TeamCreateRequest;
import com.auction.team.dto.TeamResponse;
import com.auction.team.dto.TeamUpdateRequest;
import com.auction.team.service.TeamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // ==========================================
    // CREATE TEAM
    // ==========================================

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @Valid @RequestBody TeamCreateRequest request) {

        TeamResponse response =
                teamService.createTeam(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================
    // GET ALL TEAMS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {

        return ResponseEntity.ok(
                teamService.getAllTeams());
    }

    // ==========================================
    // GET TEAM BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                teamService.getTeam(id));
    }

    // ==========================================
    // UPDATE TEAM
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable UUID id,
            @Valid @RequestBody TeamUpdateRequest request) {

        return ResponseEntity.ok(
                teamService.updateTeam(id, request));
    }

    // ==========================================
    // DELETE TEAM
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable UUID id) {

        teamService.deleteTeam(id);

        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // UPLOAD OR REPLACE TEAM LOGO
    // ==========================================

    @PostMapping(
            value = "/{id}/logo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TeamResponse> uploadLogo(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                teamService.uploadLogo(id, file));
    }

    // ==========================================
    // DELETE TEAM LOGO
    // ==========================================

    @DeleteMapping("/{id}/logo")
    public ResponseEntity<TeamResponse> deleteLogo(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                teamService.deleteLogo(id));
    }
}
