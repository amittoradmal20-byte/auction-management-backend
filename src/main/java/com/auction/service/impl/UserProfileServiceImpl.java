package com.auction.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.dto.user.UpdateUserProfileRequest;
import com.auction.dto.user.UserProfileResponse;
import com.auction.entity.UserAccount;
import com.auction.entity.UserProfile;
import com.auction.exception.ResourceNotFoundException;
import com.auction.mapper.UserProfileMapper;
import com.auction.repository.UserRepository;
import com.auction.service.UserProfileService;


@Service
@Transactional(readOnly = true)
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    
    public UserProfileServiceImpl(UserRepository userRepository,
    		UserProfileMapper userProfileMapper) {
        this.userRepository = userRepository;
        this.userProfileMapper = userProfileMapper;
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

}