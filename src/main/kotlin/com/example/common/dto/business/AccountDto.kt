package com.example.common.dto.business

import jakarta.validation.constraints.*

// TODO: make truncated version for storage in room service
data class AccountDto(
    val id: Long,

    @NotBlank
    val name: String,

    @Min(0)
    val fines: Int,

    val active: Boolean,
    val roomId: Long?
)