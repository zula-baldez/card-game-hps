package com.example.common.client

import jakarta.validation.constraints.NotBlank

data class CredentialsRequest(
    @NotBlank
    val username : String,

    @NotBlank
    val password : String)