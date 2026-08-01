package com.auction.security.jwt;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String AUTH_HEADER = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String TOKEN_TYPE = "Bearer";

    public static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000; // 1 Hour

    public static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L; // 7 Days

}