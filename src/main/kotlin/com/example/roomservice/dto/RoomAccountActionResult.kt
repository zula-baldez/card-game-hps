package com.example.roomservice.dto

import jakarta.validation.constraints.NotNull

data class RoomAccountActionResult(

    @NotNull
    val success: Boolean,
    val reason: String?
)