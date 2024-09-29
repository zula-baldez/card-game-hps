package com.example.gamehandlerservice.model.dto

import jakarta.validation.constraints.NotNull

data class AccountActionRequest(
    @NotNull
    val accountId: Long
)
