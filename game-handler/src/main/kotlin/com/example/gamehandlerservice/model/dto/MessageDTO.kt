package com.example.gamehandlerservice.model.dto

import jakarta.validation.constraints.NotBlank

data class MessageDTO(
    @field:NotBlank
    var test: String) {
}