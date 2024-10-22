package com.example.gamehandlerservice.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class AccountActionRequest(
    @NotNull
    @JsonProperty("account_id")
    val accountId: Long
)
