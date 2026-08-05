package com.auction.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.auction.dto.auth.JwtResponse;
import com.auction.dto.auth.LoginRequest;
import com.auction.dto.auth.LogoutRequest;
import com.auction.dto.auth.RefreshTokenRequest;
import com.auction.dto.auth.RegisterRequest;
import com.auction.dto.common.ApiResponse;
import com.auction.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        ApiResponse response = authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        JwtResponse response = authenticationService.login(request, httpRequest);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @Valid @RequestBody LogoutRequest request) {

        authenticationService.logout(request);

        ApiResponse response = new ApiResponse();

        response.setSuccess(true);
        response.setMessage("Logout successful");

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                authenticationService.refreshToken(request));
    }
    
    @GetMapping("/me")
    public ResponseEntity<String> me(Authentication authentication) {

        return ResponseEntity.ok(
                "Hello " + authentication.getName());
    }
}