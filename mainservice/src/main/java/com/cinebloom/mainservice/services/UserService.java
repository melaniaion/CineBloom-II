package com.cinebloom.mainservice.services;

import com.cinebloom.mainservice.domain.User;
import com.cinebloom.mainservice.dtos.UserProfileDTO;
import com.cinebloom.mainservice.mappers.UserMapper;
import com.cinebloom.mainservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    @Value("classpath:/static/images/default-user.jpg")
    private Resource defaultUserImage;

    public User ensureUserExists(JwtAuthenticationToken jwt) {
        String username = jwt.getToken().getClaim("preferred_username");
        String email = jwt.getToken().getClaim("email");

        return userRepo.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(username)
                            .email(email)
                            .bio("")
                            .build();
                    return userRepo.save(newUser);
                });
    }
    public Long getCurrentUserId(JwtAuthenticationToken jwt) {
        return getCurrentUser(jwt).getId();
    }

    public User getCurrentUser(JwtAuthenticationToken jwt) {
        System.out.println("service - melania- JWT USERNAME: " + jwt.getToken().getClaim("preferred_username"));
        String username = jwt.getToken().getClaimAsString("preferred_username");

        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public UserProfileDTO getProfile(JwtAuthenticationToken jwt) {
        User user = ensureUserExists(jwt);
        UserProfileDTO dto = userMapper.toDto(user);

        return dto;
    }

    public byte[] getProfilePicture(Long userId) {
        return userRepo.findById(userId)
                .map(User::getProfilePicture)
                .filter(pic -> pic != null && pic.length > 0)
                .orElseGet(() -> {
                    try {
                        return Files.readAllBytes(defaultUserImage.getFile().toPath());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to load default profile picture", e);
                    }
                });
    }
}