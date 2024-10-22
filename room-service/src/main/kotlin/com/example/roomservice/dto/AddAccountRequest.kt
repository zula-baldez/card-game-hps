package com.example.roomservice.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class AddAccountRequest(
    @NotNull(message = "account_id should not be null")
    @JsonProperty("account_id")
    val accountId: Long
)