package com.example.common.dto.personalaccout.business

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
    val players: List<Long>, // TODO: make AccountDto
    @JsonProperty("current_game_id")
    val currentGameId: Long,
    @JsonProperty("banned_players")
    val bannedPlayers: List<Long> = listOf() // TODO: make AccountDto
)