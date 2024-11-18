package com.example.gamehandlerservice.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Penki",
        description = "Card Game Penki",
        version = "1.0.0",
        contact = Contact(
            name = "Vereschagin Egor, Tsyu Tyanshen, Sobolev Ivan",
            email = "penki@niuitmo.ru"
        )
    )
)
class OpenApiConfig