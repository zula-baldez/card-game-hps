package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card
data class MoveCardRequest(val fromDropArea: Long, val toDropArea: Long, val card: Card)
