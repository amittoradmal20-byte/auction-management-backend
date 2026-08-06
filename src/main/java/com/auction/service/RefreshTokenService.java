package com.auction.service;

import com.auction.entity.RefreshToken;
import com.auction.entity.UserAccount;

public interface RefreshTokenService {

    /**
     * Creates a new refresh token for the user.
     *
     * Returns the plain refresh token.
     */
    String createRefreshToken(
    		UserAccount user,
            String deviceName,
            String ipAddress);

    /**
     * Validates a refresh token and returns the stored entity.
     */
    RefreshToken validateRefreshToken(String refreshToken);

    /**
     * Revoke a single refresh token.
     */
    void revokeRefreshToken(String refreshToken);
    
    /**
     *  Revoke refresh tokens .
     */
    void revokeRefreshToken(RefreshToken refreshToken);

    /**
     * Revoke all refresh tokens of a user.
     */
    void revokeAllTokens(UserAccount user);

    /**
     * Remove expired refresh tokens.
     */
    void removeExpiredTokens();
}