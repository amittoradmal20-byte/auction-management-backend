package com.auction.cricketgroup.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.cricketgroup.entity.CricketGroup;

@Repository
public interface CricketGroupRepository
        extends JpaRepository<CricketGroup, UUID> {

    boolean existsByNameIgnoreCase(String name);
}
