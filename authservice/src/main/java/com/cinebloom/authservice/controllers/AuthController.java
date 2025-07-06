package com.cinebloom.authservice.controllers;

import com.cinebloom.authservice.dtos.RegisterRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl("http://localhost:8080")
                    .realm("master")
                    .clientId("admin-cli")
                    .username("admin")
                    .password("admin")
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();

            String realmName = "cinebloom";

            List<UserRepresentation> existingUsers = keycloak.realm(realmName)
                    .users()
                    .search(request.getUsername());

            if (!existingUsers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username already exists");
            }

            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setEnabled(true);

            Response response = keycloak.realm(realmName).users().create(user);
            if (response.getStatus() != 201) {
                return ResponseEntity.status(response.getStatus())
                        .body("Failed to create user");
            }

            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            CredentialRepresentation password = new CredentialRepresentation();
            password.setTemporary(false);
            password.setType(CredentialRepresentation.PASSWORD);
            password.setValue(request.getPassword());

            keycloak.realm(realmName).users().get(userId).resetPassword(password);

            RoleRepresentation userRole = keycloak.realm(realmName)
                    .roles()
                    .get("USER")
                    .toRepresentation();

            keycloak.realm(realmName)
                    .users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(List.of(userRole));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
