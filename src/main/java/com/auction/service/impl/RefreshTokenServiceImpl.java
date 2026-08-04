package com.auction.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.entity.RefreshToken;
import com.auction.entity.User;
import com.auction.exception.InvalidRefreshTokenException;
import com.auction.repository.RefreshTokenRepository;
import com.auction.security.token.RefreshTokenGenerator;
import com.auction.security.token.RefreshTokenProperties;
import com.auction.security.token.TokenHashUtil;
import com.auction.service.interfaces.RefreshTokenService;

@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

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
            User user,
            String deviceName,
            String ipAddress) {
    	System.out.println("Refresh Token Expiration: " + refreshTokenProperties.getExpiration());

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

        return plainToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String refreshToken) {

        String tokenHash = TokenHashUtil.sha256(refreshToken);

        RefreshToken entity = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException("Invalid refresh token"));

        if (Boolean.TRUE.equals(entity.getRevoked())) {
            throw new InvalidRefreshTokenException("Refresh token has been revoked.");
        }

        if (entity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token has expired.");
        }

        return entity;
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {

        RefreshToken entity = validateRefreshToken(refreshToken);

        revokeRefreshToken(entity);
    }

    @Override
    public void revokeAllTokens(User user) {

        refreshTokenRepository
                .findByUserAndRevokedFalse(user)
                .forEach(token -> token.setRevoked(Boolean.TRUE));
    }

    @Override
    public void removeExpiredTokens() {

        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
    
    @Override
    public void revokeRefreshToken(RefreshToken refreshToken) {

        refreshToken.setRevoked(Boolean.TRUE);

        refreshToken.setLastUsedAt(LocalDateTime.now());

        if (!Boolean.TRUE.equals(refreshToken.getRevoked())) {
            refreshToken.setRevoked(Boolean.TRUE);
            refreshToken.setLastUsedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);
        }
    }
}