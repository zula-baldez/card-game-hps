package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card

data class PlayerActionRequest(
    val droppedCard: Card?,
    val action: PlayerAction
)
