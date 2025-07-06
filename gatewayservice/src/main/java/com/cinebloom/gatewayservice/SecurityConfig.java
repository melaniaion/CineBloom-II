package com.cinebloom.gatewayservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/css/**", "/js/**",       // allow static CSS/JS
                                "/images/**", "/webjars/**" // allow images and WebJars
                        ).permitAll()
                        .pathMatchers("/", "/auth/register").permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, ex2) -> Mono.empty())
                )
                .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(resource -> resource
                        .bearerTokenConverter(new CustomBearerTokenAuthenticationConverter())
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }
}

