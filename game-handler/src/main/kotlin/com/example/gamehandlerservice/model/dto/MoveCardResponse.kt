package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card
import jakarta.validation.constraints.NotNull

data class MoveCardResponse(
    val idFrom: Long?,
    val idTo: Long,
    @NotNull
    val card: Card
)