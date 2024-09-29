package com.example.roomservice.dto

import com.example.gamehandlerservice.model.dto.AccountAction

data class RemoveAccountRequest(
    val reason: AccountAction
)
