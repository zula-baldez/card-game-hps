package com.example.roomservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive


data class CreateRoomRequest(

    @NotNull
    @Positive
    val capacity: Int,

    @NotBlank
    val name: String
)