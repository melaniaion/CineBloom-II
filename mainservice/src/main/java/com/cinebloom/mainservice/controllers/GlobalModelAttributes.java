package com.cinebloom.mainservice.controllers;

import com.cinebloom.mainservice.repositories.UserRepository;
import com.cinebloom.mainservice.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UserRepository userRepository;

    @ModelAttribute("userId")
    public Long populateUserId(JwtAuthenticationToken jwt) {
        if (jwt == null) return null;
        String username = jwt.getToken().getClaimAsString("preferred_username");

        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElse(null);
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("appName", "CineBloom");
    }
}