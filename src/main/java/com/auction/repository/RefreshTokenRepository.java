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
    List<RefreshToken> findByUserAccount(UserAccount userAccount);

    /**
     * Find all active (not revoked) tokens for a user.
     */
    List<RefreshToken> findByUserAccountAndRevokedFalse(UserAccount userAccount);

    /**
     * Delete all refresh tokens for a user.
     */
    void deleteByUserAccount(UserAccount userAccount);

    /**
     * Delete expired refresh tokens.
     */
    void deleteByExpiryDateBefore(LocalDateTime dateTime);

    /**
     * Check whether a token hash exists.
     */
    boolean existsByTokenHash(String tokenHash);

}