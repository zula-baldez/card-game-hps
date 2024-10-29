package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class MoveCardRequest(
    @NotNull
    var fromDropArea: Long,

    @NotNull
    var toDropArea: Long,

    @NotNull
    @JsonProperty("card")
    val card: Card
)
