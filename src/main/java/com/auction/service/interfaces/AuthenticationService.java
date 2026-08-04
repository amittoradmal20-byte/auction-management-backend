package com.auction.service.interfaces;

import com.auction.dto.request.LoginRequest;
import com.auction.dto.request.LogoutRequest;
import com.auction.dto.request.RefreshTokenRequest;
import com.auction.dto.request.RegisterRequest;
import com.auction.dto.response.ApiResponse;
import com.auction.dto.response.JwtResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    ApiResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request,  HttpServletRequest httpRequest);
    
    JwtResponse refreshToken(RefreshTokenRequest request);
    
    void logout(LogoutRequest request);

}
