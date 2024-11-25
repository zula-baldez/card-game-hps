package com.example.authservice.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server

@OpenAPIDefinition(
    info = Info(
        title = "Penki",
        description = "Card Game Penki",
        version = "1.0.0",
        contact = Contact(
            name = "Vereschagin Egor, Tsyu Tyanshen, Sobolev Ivan",
            email = "penki@niuitmo.ru"
        )
    ),
    servers = [Server(url = "/auth-service", description = "Gateway server")]
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
class OpenApiConfig