package com.cinebloom.authservice.controllers;

import com.cinebloom.authservice.dtos.RegisterRequest;
import com.cinebloom.authservice.services.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        keycloakService.registerUser(request);
        return "User registered successfully";
    }
}