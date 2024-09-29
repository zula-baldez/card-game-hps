package com.example.gamehandlerservice.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AccountActionDTO(
    @NotBlank
    val accountAction: AccountAction,
    val id: Long,
    @NotBlank
    val name: String
) {
}