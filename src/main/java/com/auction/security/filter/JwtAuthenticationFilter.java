package com.auction.security.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auction.security.jwt.JwtService;
import com.auction.security.service.CustomUserDetailsService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("Processing request: {} {}",
                request.getMethod(),
                request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            log.debug("No Bearer token found for request '{}'.",
                    request.getRequestURI());

            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        String username;

        try {

            username = jwtService.extractUsername(jwt);

            log.debug("JWT belongs to user '{}'.", username);

        } catch (JwtException | IllegalArgumentException ex) {

            log.warn("Invalid or expired JWT received for request '{}'. Reason: {}",
                    request.getRequestURI(),
                    ex.getMessage());

            filterChain.doFilter(request, response);
            return;
        }

        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                log.info("User '{}' authenticated successfully.",
                        username);

            } else {

                log.warn("JWT validation failed for user '{}'.",
                        username);
            }

        } else if (SecurityContextHolder.getContext().getAuthentication() != null) {

            log.debug("SecurityContext already contains an authenticated user.");
        }

        filterChain.doFilter(request, response);

        log.debug("Completed request: {} {}",
                request.getMethod(),
                request.getRequestURI());
    }
}