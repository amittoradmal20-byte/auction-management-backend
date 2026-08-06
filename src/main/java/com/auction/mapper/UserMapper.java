package com.auction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.auction.dto.auth.RegisterRequest;
import com.auction.entity.UserAccount;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountLocked", ignore = true)
    @Mapping(target = "accountExpired", ignore = true)
    @Mapping(target = "credentialsExpired", ignore = true)
    UserAccount toEntity(RegisterRequest request);
}