package com.example.common.dto.roomservice

import com.example.common.dto.personalaccout.AccountDto
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class RoomDto(
    val id: Long,
    @field:NotBlank
    val name: String,
    @JsonProperty("host_id")
    val hostId: Long,
    @field:Positive
    val capacity: Int,
    val players: List<AccountDto>,
    @JsonProperty("current_game_id")
    val currentGameId: Long,
    @JsonProperty("banned_players")
    val bannedPlayers: List<AccountDto> = listOf()
)