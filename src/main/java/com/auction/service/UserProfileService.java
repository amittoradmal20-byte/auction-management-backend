package com.auction.service;

import com.auction.dto.user.ChangePasswordRequest;
import com.auction.dto.user.UpdateUserProfileRequest;
import com.auction.dto.user.UserProfileResponse;

public interface UserProfileService {

	 /**
     * Returns the profile of the currently authenticated user.
     *
     * @return authenticated user's profile
     */
    UserProfileResponse getMyProfile();
    
    UserProfileResponse updateMyProfile(UpdateUserProfileRequest request);
    
    void changePassword(ChangePasswordRequest request);
}