package com.example.common.dto.business

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

// TODO lab2: make truncated version for storage in room service
data class AccountDto(
    val id: Long,

    @get:NotBlank
    val name: String,

    @get:Min(0)
    val fines: Int,
    @JsonProperty("room_id")
    val roomId: Long?
)