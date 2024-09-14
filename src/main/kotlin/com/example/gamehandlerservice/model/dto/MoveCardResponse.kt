package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card

data class MoveCardResponse(val idFrom: Long?, val idTo: Long, val card: Card) {
}