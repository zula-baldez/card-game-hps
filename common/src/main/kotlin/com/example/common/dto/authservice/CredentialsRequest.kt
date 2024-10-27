package com.example.common.dto.authservice

import jakarta.validation.constraints.NotBlank

data class CredentialsRequest(
    @NotBlank
    val username : String,

    @NotBlank
    val password : String
)