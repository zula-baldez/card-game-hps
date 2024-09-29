package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card
import jakarta.validation.constraints.NotNull

data class MoveCardRequest(
    @NotNull
    val fromDropArea: Long,

    @NotNull
    val toDropArea: Long,

    @NotNull
    val card: Card
)
