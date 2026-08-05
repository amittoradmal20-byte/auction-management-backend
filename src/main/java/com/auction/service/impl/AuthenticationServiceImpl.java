package com.auction.service.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.auction.security.userdetails.CustomUserDetails;
import com.auction.service.AuthenticationService;
import com.auction.service.RefreshTokenService;
import com.auction.dto.auth.JwtResponse;
import com.auction.dto.auth.LoginRequest;
import com.auction.dto.auth.LogoutRequest;
import com.auction.dto.auth.RefreshTokenRequest;
import com.auction.dto.auth.RegisterRequest;
import com.auction.dto.common.ApiResponse;
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
import com.auction.util.RequestUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
	
	 private static final Logger log =
	            LoggerFactory.getLogger(AuthenticationServiceImpl.class);

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

        log.info("Registration request received for username '{}'.",
                request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {

            log.warn("Registration failed. Username '{}' already exists.",
                    request.getUsername());

            throw new DuplicateResourceException("Username already exists.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {

            log.warn("Registration failed. Email '{}' already exists.",
                    request.getEmail());

            throw new DuplicateResourceException("Email already exists.");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setAccountExpired(false);
        user.setCredentialsExpired(false);

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> {
                    log.error("Default USER role not found.");
                    return new ResourceNotFoundException("Default USER role not found.");
                });

        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        log.info("User '{}' registered successfully.",
                user.getUsername());

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setMessage("User registered successfully.");

        return response;
    }

    @Override
    public JwtResponse login(LoginRequest request,
                             HttpServletRequest httpRequest) {

        log.info("Login request received for username '{}'.",
                request.getUsername());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()));

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        log.debug("Authentication successful for user '{}'.",
                user.getUsername());

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                refreshTokenService.createRefreshToken(
                        user,
                        RequestUtil.getUserAgent(httpRequest),
                        RequestUtil.getClientIp(httpRequest));

        JwtResponse response = new JwtResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setUsername(user.getUsername());
        response.setExpiresIn(jwtProperties.getAccessTokenExpiration());

        log.info("User '{}' logged in successfully.",
                user.getUsername());

        return response;
    }
    
    @Override
    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest request) {

        log.info("Refresh token request received.");

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken());

        User user = refreshToken.getUser();

        log.debug("Generating new access token for user '{}'.",
                user.getUsername());

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

        log.info("Access token and refresh token regenerated successfully for user '{}'.",
                user.getUsername());

        return response;
    }
    
    @Override
    @Transactional
    public void logout(LogoutRequest request) {

        log.info("Logout request received.");

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken());

        refreshTokenService.revokeRefreshToken(refreshToken);

        log.info("User '{}' logged out successfully.",
                refreshToken.getUser().getUsername());
    }
    
    
}