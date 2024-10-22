package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class MoveCardResponse(
    @JsonProperty("id_from")
    val idFrom: Long?,
    @JsonProperty("id_to")
    val idTo: Long,
    @NotNull
    @JsonProperty("card")
    val card: Card
)