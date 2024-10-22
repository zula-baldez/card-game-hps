package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Card
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class MoveCardRequest(
    @NotNull
    @JsonProperty("from_drop_area")
    var fromDropArea: Long,

    @NotNull
    @JsonProperty("to_drop_area")
    var toDropArea: Long,

    @NotNull
    @JsonProperty("card")
    val card: Card
)
