package com.example.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayServerConfiguration {
    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("personal-account") { r ->
                r.path("/gateway/personal-account/**")
                    .filters { f -> f.stripPrefix(2) }
                    .uri("lb://personal-account:8083")
            }
            .route("auth-service") { r ->
                r.path("/gateway/auth-service/**")
                    .filters { f -> f.stripPrefix(2) }
                    .uri("lb://auth-service:8081")
            }
            .route("game-handler") { r ->
                r.path("/gateway/game-handler/**")
                    .filters { f -> f.stripPrefix(2) }
                    .uri("lb://game-handler:8082")
            }
            .route("room-service") { r ->
                r.path("/gateway/room-service/**")
                    .filters { f -> f.stripPrefix(2) }
                    .uri("lb://room-service:8084")
            }
            .route("avatars-service") { r ->
                r.path("/gateway/avatars-service/**")
                    .filters { f -> f.stripPrefix(2) }
                    .uri("lb://avatars-service:8086")
            }
            .build()
    }
}