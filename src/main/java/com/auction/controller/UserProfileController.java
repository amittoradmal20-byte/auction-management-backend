package com.auction.controller;

import com.auction.dto.user.UpdateUserProfileRequest;
import com.auction.dto.user.UserProfileResponse;
import com.auction.service.AuthenticationService;
import com.auction.service.UserProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserProfileService userProfileService;
    
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }


    /**
     * Get the profile of the currently authenticated user.
     *
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {

        UserProfileResponse response = userProfileService.getMyProfile();

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {

        UserProfileResponse response = userProfileService.updateMyProfile(request);

        return ResponseEntity.ok(response);
    }

}