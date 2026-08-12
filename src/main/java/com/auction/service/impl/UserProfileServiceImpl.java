package com.auction.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.dto.user.ChangePasswordRequest;
import com.auction.dto.user.UpdateUserProfileRequest;
import com.auction.dto.user.UserProfileResponse;
import com.auction.entity.UserAccount;
import com.auction.entity.UserProfile;
import com.auction.exception.ResourceNotFoundException;
import com.auction.mapper.UserProfileMapper;
import com.auction.repository.RefreshTokenRepository;
import com.auction.repository.UserRepository;
import com.auction.service.UserProfileService;


@Service
@Transactional(readOnly = true)
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    
    public UserProfileServiceImpl(UserRepository userRepository,
    		UserProfileMapper userProfileMapper,
    		PasswordEncoder passwordEncoder,
    		RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.userProfileMapper = userProfileMapper;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public UserProfileResponse getMyProfile() {

        String username = getCurrentUsername();

        UserAccount user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        return userProfileMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateMyProfile(UpdateUserProfileRequest request) {

        String username = getCurrentUsername();

        UserAccount user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        UserProfile profile = user.getUserProfile();

        if (profile == null) {
            throw new ResourceNotFoundException("User profile not found.");
        }

        userProfileMapper.updateProfile(request, profile);

        // Optional because profile is a managed entity and Hibernate dirty checking
        // will persist changes automatically at transaction commit.
        // userRepository.save(user);

        return userProfileMapper.toResponse(user);
    }

    /**
     * Returns the username of the currently authenticated user.
     */
    private String getCurrentUsername() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return authentication.getName();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        String username = getCurrentUsername();

        UserAccount user = userRepository.findByUsernameWithProfile(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        // 1. Verify current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Current password is incorrect.");
        }

        // 2. Verify new password confirmation
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match.");
        }

        // 3. Prevent password reuse
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from the current password.");
        }

        // 4. Encode and update password
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        // 5. Revoke all existing refresh tokens
        refreshTokenRepository.revokeAllByUser(user);

        // No explicit save() is strictly required here.
        // user is a managed entity inside the transaction.
    }

}