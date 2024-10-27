package com.example.common.dto.authservice

data class GenerateServiceTokenRequest(
    val userId: Long,
    val serviceName: String
)
