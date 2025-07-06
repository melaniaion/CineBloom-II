package com.cinebloom.gatewayservice;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("authservice_route", r -> r
                        .path("/auth/**")
                        .uri("lb://AUTHSERVICE")
                )

                .route("mainservice_route", r -> r
                        .path("/cinebloom/main/**")
                        .filters(f -> f.rewritePath("/cinebloom/main/(?<segment>.*)", "/${segment}")
                                .filter((exchange, chain) -> {
                                    long start = System.currentTimeMillis();
                                    return chain.filter(exchange).then(
                                            Mono.fromRunnable(() -> {
                                                long duration = System.currentTimeMillis() - start;
                                                exchange.getResponse().getHeaders()
                                                        .add("X-Response-Time", duration + "ms");
                                            })
                                    );
                                })
                        )
                        .uri("lb://MAINSERVICE"))

                .route("test_route", r -> r
                        .path("/test/hello")
                        .filters(f -> f.rewritePath("/test/hello", "/actuator/health"))
                        .uri("lb://AUTHSERVICE"))


                .build();
    }
}

