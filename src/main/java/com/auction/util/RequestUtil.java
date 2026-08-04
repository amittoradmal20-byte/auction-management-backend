package com.auction.util;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestUtil {

    private RequestUtil() {
    }

    /**
     * Returns the real client IP.
     */
    public static String getClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Returns browser / device information.
     */
    public static String getUserAgent(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        return (userAgent == null || userAgent.isBlank())
                ? "Unknown Device"
                : userAgent;
    }
}