package com.example.authservice.dto

import jakarta.validation.constraints.NotBlank

data class CredentialsRequest(
    @NotBlank
    val username : String,

    @NotBlank
    val password : String)