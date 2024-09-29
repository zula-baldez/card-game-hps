package com.example.roomservice.dto

import jakarta.validation.constraints.NotNull

data class AddAccountRequest(
    @NotNull
    val accountId: Long
)