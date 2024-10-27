package com.example

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
                r.path("/personal-account/**")
                    .filters { f -> f.stripPrefix(1) }
                    .uri("lb://personal-account:8083")
            }
            .route("auth-service") { r ->
                r.path("/auth-service/**")
                    .filters { f -> f.stripPrefix(1) }
                    .uri("lb://auth-service:8081")
            }
            .route("game-handler") { r ->
                r.path("/game-handler/**")
                    .filters { f -> f.stripPrefix(1) }
                    .uri("lb://game-handler:8082")
            }
            .route("room-service") { r ->
                r.path("/room-service/**")
                    .filters { f -> f.stripPrefix(1) }
                    .uri("lb://room-service:8084")
            }
            .build()
    }
}