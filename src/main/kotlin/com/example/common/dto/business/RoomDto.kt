package com.example.common.dto.business

data class RoomDto(
    val id: Long,
    val name: String,
    val hostId: Long,
    val capacity: Int,
    val players: List<AccountDto>,
    val currentGameId: Long
)