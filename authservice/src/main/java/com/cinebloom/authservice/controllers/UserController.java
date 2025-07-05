package com.cinebloom.authservice.controllers;

import com.cinebloom.authservice.domain.User;
import com.cinebloom.authservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String id = jwt.getSubject(); // sub = user id from Keycloak
        String username = jwt.getClaim("preferred_username");

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User profile not found. Please register."));
    }
}