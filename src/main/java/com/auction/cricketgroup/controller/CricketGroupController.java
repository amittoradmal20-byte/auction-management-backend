package com.auction.cricketgroup.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auction.cricketgroup.dto.CricketGroupCreateRequest;
import com.auction.cricketgroup.dto.CricketGroupResponse;
import com.auction.cricketgroup.service.CricketGroupService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cricket-groups")
public class CricketGroupController {

    private final CricketGroupService cricketGroupService;
    
	public CricketGroupController(CricketGroupService cricketGroupService) {
		this.cricketGroupService = cricketGroupService;
	}

    @PostMapping
    public ResponseEntity<CricketGroupResponse> createGroup(
            @Valid @RequestBody CricketGroupCreateRequest request) {

        CricketGroupResponse response =
                cricketGroupService.createGroup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<CricketGroupResponse> getGroupById(
            @PathVariable UUID groupId) {

        return ResponseEntity.ok(
                cricketGroupService.getGroupById(groupId));
    }

    @GetMapping
    public ResponseEntity<List<CricketGroupResponse>> getAllGroups() {

        return ResponseEntity.ok(
                cricketGroupService.getAllGroups());
    }
}
