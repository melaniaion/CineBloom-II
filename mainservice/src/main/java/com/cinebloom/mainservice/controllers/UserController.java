package com.cinebloom.mainservice.controllers;

import com.cinebloom.mainservice.dtos.UserProfileDTO;
import com.cinebloom.mainservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public String getProfile(JwtAuthenticationToken jwt, Model model) {
        try {
            UserProfileDTO profile = userService.getProfile(jwt);
            model.addAttribute("profile", profile);
        } catch (Exception e) {
            model.addAttribute("ProfileError", e.getMessage());
        }

        return "profile";
    }

    @GetMapping("/users/{id}/picture")
    public ResponseEntity<byte[]> getPicture(@PathVariable Long id) {
        byte[] pic = userService.getProfilePicture(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(pic);
    }
}