package com.auction.service.impl;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.auction.security.userdetails.CustomUserDetails;

import com.auction.dto.request.LoginRequest;
import com.auction.dto.request.LogoutRequest;
import com.auction.dto.request.RefreshTokenRequest;
import com.auction.dto.request.RegisterRequest;
import com.auction.dto.response.ApiResponse;
import com.auction.dto.response.JwtResponse;
import com.auction.entity.RefreshToken;
import com.auction.entity.Role;
import com.auction.entity.User;
import com.auction.exception.DuplicateResourceException;
import com.auction.exception.ResourceNotFoundException;
import com.auction.mapper.UserMapper;
import com.auction.repository.RoleRepository;
import com.auction.repository.UserRepository;
import com.auction.security.jwt.JwtProperties;
import com.auction.security.jwt.JwtService;
import com.auction.service.interfaces.AuthenticationService;
import com.auction.service.interfaces.RefreshTokenService;
import com.auction.util.RequestUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            AuthenticationManager authenticationManager,
            JwtProperties jwtProperties) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
        	throw new DuplicateResourceException("Username already exists.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
        	throw new DuplicateResourceException("Email already exists.");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setAccountExpired(false);
        user.setCredentialsExpired(false);

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found."));

        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setMessage("User registered successfully.");

        return response;
    }

    @Override
    public JwtResponse login(LoginRequest request,  HttpServletRequest httpRequest) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()));

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userDetails);

        User user = userDetails.getUser();

        String refreshToken =
        		refreshTokenService.createRefreshToken(
        		        user,
        		        RequestUtil.getUserAgent(httpRequest),
        		        RequestUtil.getClientIp(httpRequest));

        JwtResponse response = new JwtResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setUsername(userDetails.getUsername());
     // Set access token expiration in milliseconds
        response.setExpiresIn(jwtProperties.getAccessTokenExpiration());

        return response;
    }
    
    @Override
    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken());

        User user = refreshToken.getUser();

        CustomUserDetails userDetails =
                new CustomUserDetails(user);
        String accessToken =
                jwtService.generateAccessToken(userDetails);
        refreshTokenService.revokeRefreshToken(refreshToken);
        String newRefreshToken =
                refreshTokenService.createRefreshToken(
                        user,
                        refreshToken.getDeviceName(),
                        refreshToken.getIpAddress());
        JwtResponse response = new JwtResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
        response.setTokenType("Bearer");
        response.setUsername(user.getUsername());
        response.setExpiresIn(jwtProperties.getAccessTokenExpiration());

        return response;
        
    }
    
    @Override
    @Transactional
    public void logout(LogoutRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken());

        refreshTokenService.revokeRefreshToken(refreshToken);
    }
    
    
}