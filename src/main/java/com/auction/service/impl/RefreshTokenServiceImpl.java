package com.auction.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.entity.RefreshToken;
import com.auction.entity.UserAccount;
import com.auction.exception.InvalidRefreshTokenException;
import com.auction.repository.RefreshTokenRepository;
import com.auction.security.token.RefreshTokenGenerator;
import com.auction.security.token.RefreshTokenProperties;
import com.auction.security.token.TokenHashUtil;
import com.auction.service.RefreshTokenService;

@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger log =
            LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final RefreshTokenProperties refreshTokenProperties;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenGenerator refreshTokenGenerator,
            RefreshTokenProperties refreshTokenProperties) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.refreshTokenProperties = refreshTokenProperties;
    }

    @Override
    public String createRefreshToken(
    		UserAccount user,
            String deviceName,
            String ipAddress) {

        log.info("Creating refresh token for user '{}'.", user.getUsername());

        String plainToken = refreshTokenGenerator.generate();
        String tokenHash = TokenHashUtil.sha256(plainToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusSeconds(refreshTokenProperties.getExpiration() / 1000));
        refreshToken.setRevoked(Boolean.FALSE);
        refreshToken.setDeviceName(deviceName);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setLastUsedAt(LocalDateTime.now());
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);

        log.info(
                "Refresh token created successfully for user '{}'. Device='{}', IP='{}'.",
                user.getUsername(),
                deviceName,
                ipAddress);

        return plainToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String refreshToken) {

        log.debug("Validating refresh token.");

        String tokenHash = TokenHashUtil.sha256(refreshToken);

        RefreshToken entity = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> {
                    log.warn("Refresh token validation failed. Token not found.");
                    return new InvalidRefreshTokenException("Invalid refresh token.");
                });

        if (Boolean.TRUE.equals(entity.getRevoked())) {

            log.warn(
                    "Revoked refresh token used by user '{}'.",
                    entity.getUser().getUsername());

            throw new InvalidRefreshTokenException(
                    "Refresh token has been revoked.");
        }

        if (entity.getExpiryDate().isBefore(LocalDateTime.now())) {

            log.warn(
                    "Expired refresh token used by user '{}'.",
                    entity.getUser().getUsername());

            throw new InvalidRefreshTokenException(
                    "Refresh token has expired.");
        }

        log.debug(
                "Refresh token validated successfully for user '{}'.",
                entity.getUser().getUsername());

        return entity;
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {

        RefreshToken entity = validateRefreshToken(refreshToken);

        revokeRefreshToken(entity);

        log.info(
                "Refresh token revoked successfully for user '{}'.",
                entity.getUser().getUsername());
    }

    @Override
    public void revokeAllTokens(UserAccount user) {

        log.info("Revoking all active refresh tokens for user '{}'.",
                user.getUsername());

        refreshTokenRepository
                .findByUserAndRevokedFalse(user)
                .forEach(token -> {
                    token.setRevoked(Boolean.TRUE);
                    token.setLastUsedAt(LocalDateTime.now());
                });

        log.info("All refresh tokens revoked for user '{}'.",
                user.getUsername());
    }

    @Override
    public void removeExpiredTokens() {

        log.info("Removing expired refresh tokens.");

        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());

        log.info("Expired refresh tokens removed successfully.");
    }

    @Override
    public void revokeRefreshToken(RefreshToken refreshToken) {

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {

            log.debug(
                    "Refresh token is already revoked for user '{}'.",
                    refreshToken.getUser().getUsername());

            return;
        }

        refreshToken.setRevoked(Boolean.TRUE);
        refreshToken.setLastUsedAt(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);

        log.info(
                "Refresh token revoked successfully for user '{}'.",
                refreshToken.getUser().getUsername());
    }
}