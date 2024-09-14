package com.example.gamehandlerservice.model.dto

data class CardDropValidationResult(val changeTurn: Boolean, val valid: Boolean, val needsFine: Boolean, val needToChangeStage: Boolean)
