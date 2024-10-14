package com.example.common.dto.business

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class RoomDto(
    val id: Long,

    @get:NotBlank
    val name: String,

    @JsonProperty("host_id")
    val hostId: Long,

    @get:Positive
    val capacity: Int,
    val players: List<AccountDto>,
    @JsonProperty("current_game_id")
    val currentGameId: Long,
    @JsonProperty("banned_players")
    val bannedPlayers: List<AccountDto> = listOf()
)