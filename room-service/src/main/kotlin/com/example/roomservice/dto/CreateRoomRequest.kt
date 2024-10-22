package com.example.roomservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive


data class CreateRoomRequest(

    @NotNull(message = "capacity should not be null")
    @field:Positive(message = "capacity should be greater than 0")
    val capacity: Int,

    @field:NotBlank(message = "name should not be blank")
    val name: String
)