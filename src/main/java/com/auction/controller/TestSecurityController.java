package com.auction.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSecurityController {

    @GetMapping("/api/v1/test/authenticated")
    public String authenticated() {
        return "Authenticated User";
    }

    @GetMapping("/api/v1/test/user")
    @PreAuthorize("hasRole('USER')")
    public String user() {
        return "ROLE_USER Success";
    }

    @GetMapping("/api/v1/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "ROLE_ADMIN Success";
    }

    @GetMapping("/api/v1/test/player-create")
    @PreAuthorize("hasAuthority('PLAYER_CREATE')")
    public String playerCreate() {
        return "PLAYER_CREATE Success";
    }
}