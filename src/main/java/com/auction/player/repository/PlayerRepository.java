package com.auction.player.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.player.entity.Player;
import com.auction.player.enums.PlayerRole;
import com.auction.player.enums.PlayerStatus;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    boolean existsByDisplayNameIgnoreCase(String displayName);

    List<Player> findByStatus(PlayerStatus status);

    List<Player> findByRole(PlayerRole role);

    List<Player> findByStatusAndRole(
            PlayerStatus status,
            PlayerRole role);
}