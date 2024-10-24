package com.example.common.dto.roomservice

import com.example.common.dto.personalaccout.business.AccountAction
import jakarta.validation.constraints.NotNull

data class RemoveAccountRequest(

    @NotNull(message = "reason should not be null")
    val reason: AccountAction
)
