package com.example.roomservice.dto

import com.example.gamehandlerservice.model.dto.AccountAction
import jakarta.validation.constraints.NotNull

data class RemoveAccountRequest(

    @NotNull
    val reason: AccountAction
)
