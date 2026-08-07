package com.auction.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.entity.UserProfile;

@Repository
public interface UserProfileRepository
        extends JpaRepository<UserProfile, UUID> {

}