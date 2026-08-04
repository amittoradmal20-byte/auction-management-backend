package com.auction.security.token;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class RefreshTokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final int TOKEN_LENGTH = 64;

    public String generate() {

        byte[] randomBytes = new byte[TOKEN_LENGTH];

        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

}