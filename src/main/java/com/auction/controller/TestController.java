package com.auction.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/hello")
    public String hello(Authentication authentication) {

        return "Hello " + authentication.getName()
                + ", JWT Authentication is working!";
    }

}