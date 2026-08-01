package com.auction.service.interfaces;

import com.auction.dto.request.LoginRequest;
import com.auction.dto.request.RegisterRequest;
import com.auction.dto.response.ApiResponse;
import com.auction.dto.response.JwtResponse;

public interface AuthenticationService {

    ApiResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request);

}
