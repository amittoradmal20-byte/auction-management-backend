package com.auction.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.entity.RefreshToken;
import com.auction.entity.UserAccount;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID > {

    /**
     * Find refresh token by hash.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Find all tokens for a user.
     */
    List<RefreshToken> findByUser(UserAccount user);

    /**
     * Find all active (not revoked) tokens for a user.
     */
    List<RefreshToken> findByUserAndRevokedFalse(UserAccount user);

    /**
     * Delete all refresh tokens for a user.
     */
    void deleteByUser(UserAccount user);

    /**
     * Delete expired refresh tokens.
     */
    void deleteByExpiryDateBefore(LocalDateTime dateTime);

    /**
     * Check whether a token hash exists.
     */
    boolean existsByTokenHash(String tokenHash);

}