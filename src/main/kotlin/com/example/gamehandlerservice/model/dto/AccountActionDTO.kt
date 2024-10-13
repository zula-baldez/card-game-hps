package com.example.gamehandlerservice.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class AccountActionDTO(
    @NotBlank
    @JsonProperty("account_action")
    val accountAction: AccountAction,
    val id: Long,
    @NotBlank
    val name: String
)