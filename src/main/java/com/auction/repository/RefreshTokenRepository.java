package com.auction.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.entity.RefreshToken;
import com.auction.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token by hash.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Find all tokens for a user.
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Find all active (not revoked) tokens for a user.
     */
    List<RefreshToken> findByUserAndRevokedFalse(User user);

    /**
     * Delete all refresh tokens for a user.
     */
    void deleteByUser(User user);

    /**
     * Delete expired refresh tokens.
     */
    void deleteByExpiryDateBefore(LocalDateTime dateTime);

    /**
     * Check whether a token hash exists.
     */
    boolean existsByTokenHash(String tokenHash);

}