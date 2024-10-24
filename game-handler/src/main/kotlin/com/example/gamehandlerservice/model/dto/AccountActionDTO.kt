package com.example.gamehandlerservice.model.dto

import com.example.common.dto.personalaccout.business.AccountAction
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AccountActionDTO(
    @field:NotNull
    @JsonProperty("account_action")
    val accountAction: AccountAction,
    val id: Long,
    @field:NotBlank
    val name: String
)