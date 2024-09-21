package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Stage

data class CardDropValidationResult(
    val changeTurn: Boolean,
    val valid: Boolean,
    val needsFine: Boolean,
    val nextStage: Stage?
)
