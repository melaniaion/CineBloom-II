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

                .route("static_resources", r -> r
                        .path("/css/**", "/js/**", "/images/**", "/webjars/**")
                        .filters(f -> f.rewritePath("(?<segment>/.*)", "/${segment}"))
                        .uri("lb://mainservice"))

                .route("mainservice_route", r -> r
                        .path("/**")
                        .uri("lb://MAINSERVICE"))

                .route("homepage_route", r -> r
                        .path("/")
                        .uri("lb://MAINSERVICE"))

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

