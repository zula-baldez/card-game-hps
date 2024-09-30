package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card
import jakarta.validation.constraints.NotNull

data class MoveCardRequest(
    @NotNull
    var fromDropArea: Long,

    @NotNull
    var toDropArea: Long,

    @NotNull
    val card: Card
)
