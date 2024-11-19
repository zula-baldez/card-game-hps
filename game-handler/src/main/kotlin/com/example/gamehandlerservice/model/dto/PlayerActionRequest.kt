package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card

data class PlayerActionRequest(
    val playerId: Long = 0,
    val droppedCard: Card?,
    val action: PlayerAction
)
