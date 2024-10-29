package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card

data class MoveCardRequest(
    var fromDropArea: Long = 0,
    var toDropArea: Long = 0,
    val card: Card = Card()
)
