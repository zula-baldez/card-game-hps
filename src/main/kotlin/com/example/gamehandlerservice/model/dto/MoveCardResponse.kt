package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class MoveCardResponse(
    @JsonProperty("idFrom")
    val idFrom: Long?,
    @JsonProperty("idTo")
    val idTo: Long,
    @NotNull
    @JsonProperty("card")
    val card: Card
)