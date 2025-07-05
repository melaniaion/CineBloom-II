package com.cinebloom.authservice.services;

import com.cinebloom.authservice.dtos.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final WebClient.Builder webClientBuilder;

    @Value("${keycloak.admin.base-url}")
    private String baseUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    public void registerUser(RegisterRequest request) {
        String token = fetchAdminAccessToken();

        String url = baseUrl + "/admin/realms/" + realm + "/users";

        String body = """
        {
          "username": "%s",
          "email": "%s",
          "enabled": true,
          "credentials": [
            {
              "type": "password",
              "value": "%s",
              "temporary": false
            }
          ]
        }
        """.formatted(request.getUsername(), request.getEmail(), request.getPassword());

        WebClient client = webClientBuilder.build();

        client.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(body), String.class)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private String fetchAdminAccessToken() {
        WebClient client = webClientBuilder.build();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        return client.post()
                .uri(baseUrl + "/realms/master/protocol/openid-connect/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> (String) body.get("access_token"))
                .block();
    }
}