package com.auction.service;

import com.auction.dto.auth.JwtResponse;
import com.auction.dto.auth.LoginRequest;
import com.auction.dto.auth.LogoutRequest;
import com.auction.dto.auth.RefreshTokenRequest;
import com.auction.dto.auth.RegisterRequest;
import com.auction.dto.common.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    ApiResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request,  HttpServletRequest httpRequest);
    
    JwtResponse refreshToken(RefreshTokenRequest request);
    
    void logout(LogoutRequest request);

}
