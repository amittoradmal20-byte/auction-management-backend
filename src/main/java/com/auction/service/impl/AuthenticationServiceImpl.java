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
import com.auction.dto.request.RegisterRequest;
import com.auction.dto.response.ApiResponse;
import com.auction.dto.response.JwtResponse;
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

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            JwtProperties jwtProperties) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
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
    public JwtResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()));

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userDetails);

        String refreshToken = jwtService.generateRefreshToken(userDetails);

        JwtResponse response = new JwtResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setUsername(userDetails.getUsername());
     // Set access token expiration in milliseconds
        response.setExpiresIn(jwtProperties.getAccessTokenExpiration());

        return response;
    }
}