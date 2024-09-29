package com.example.common.dto.business

import jakarta.validation.constraints.*

data class RoomDto(
    val id: Long,

    @NotBlank
    val name: String,

    val hostId: Long,

    @Positive
    val capacity: Int,
    val players: List<AccountDto>,
    val currentGameId: Long,
    val bannedPlayers: List<AccountDto>
)