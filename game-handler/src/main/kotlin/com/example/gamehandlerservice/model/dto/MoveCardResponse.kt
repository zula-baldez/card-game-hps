package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card

data class MoveCardResponse(
    var idFrom: Long? = 0,
    var idTo: Long = 0,
    var card: Card = Card()
)