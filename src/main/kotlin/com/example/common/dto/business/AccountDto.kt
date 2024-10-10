package com.example.common.dto.business

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

// TODO lab2: make truncated version for storage in room service
data class AccountDto(
    val id: Long,

    @NotBlank
    val name: String,

    @Min(0)
    val fines: Int,
    val roomId: Long?
)