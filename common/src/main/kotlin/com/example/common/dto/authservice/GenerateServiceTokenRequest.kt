package com.example.common.dto.authservice

import com.fasterxml.jackson.annotation.JsonProperty

data class GenerateServiceTokenRequest(
    @JsonProperty("user_id")
    val userId: Long,
    @JsonProperty("service_name")
    val serviceName: String
)
