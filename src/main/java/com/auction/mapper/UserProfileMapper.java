package com.auction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.auction.dto.user.UpdateUserProfileRequest;
import com.auction.dto.user.UserProfileResponse;
import com.auction.entity.UserAccount;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "enabled", source = "enabled")

    @Mapping(target = "firstName", source = "userProfile.firstName")
    @Mapping(target = "lastName", source = "userProfile.lastName")
    @Mapping(target = "email", source = "userProfile.email")
    @Mapping(target = "phone", source = "userProfile.phone")
    @Mapping(target = "dateOfBirth", source = "userProfile.dateOfBirth")
    @Mapping(target = "gender", source = "userProfile.gender")
    @Mapping(target = "address", source = "userProfile.address")
    @Mapping(target = "profileImage", source = "userProfile.profileImage")
    UserProfileResponse toResponse(UserAccount userAccount);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userAccount", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    void updateProfile(UpdateUserProfileRequest request,
                       @MappingTarget com.auction.entity.UserProfile profile);
}