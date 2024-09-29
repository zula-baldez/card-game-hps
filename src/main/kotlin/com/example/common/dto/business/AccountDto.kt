package com.example.common.dto.business

// TODO: make truncated version for storage in room service
data class AccountDto(
    val id: Long,
    val name: String,
    val fines: Int,
    val active: Boolean,
    val roomId: Long?
)