package com.gearpc.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("media-service", r -> r
                        .path("/api/v1/file/**")
                        //.uri("http://localhost:8085"))
                        .uri("lb://MEDIA-SERVICE"))
                .route("user-service", r -> r
                        .path("/api/v1/users/**", "/api/v1/admin/users/**", "/api/v1/auth/**", "/api/v1/redis/**",
                                "/login", "/login/**", "/oauth2/**")
                        .uri("lb://USER-SERVICE"))
                .build();
    }
}
