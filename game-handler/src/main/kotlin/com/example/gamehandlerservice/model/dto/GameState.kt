package com.example.gamehandlerservice.model.dto

data class GameState(
    val attackPlayer: Long,
    val defendPlayer: Long,
    val isDefending: Boolean
)
