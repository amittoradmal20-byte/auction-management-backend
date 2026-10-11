package com.auction.cricketgroup.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.cricketgroup.entity.CricketGroupPlayer;

@Repository
public interface CricketGroupPlayerRepository
        extends JpaRepository<CricketGroupPlayer, UUID> {

    boolean existsByCricketGroupIdAndPlayerId(
            UUID cricketGroupId,
            UUID playerId);

    List<CricketGroupPlayer> findByCricketGroupId(
            UUID cricketGroupId);

    List<CricketGroupPlayer> findByCricketGroupIdAndActive(
            UUID cricketGroupId,
            boolean active);

    long countByCricketGroupIdAndActive(
            UUID cricketGroupId,
            boolean active);
}
