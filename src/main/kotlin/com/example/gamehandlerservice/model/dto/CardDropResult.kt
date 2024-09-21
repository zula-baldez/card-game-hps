package com.example.gamehandlerservice.model.dto

import com.example.gamehandlerservice.model.game.Stage

data class CardDropResult(
    val changeTurn: Boolean,
    val valid: Boolean,
    val needsFine: Boolean,
    val nextStage: Stage?
) {
    companion object Constans {
        val invalid = CardDropResult(changeTurn = true, valid = false, needsFine = true, nextStage = null)
        val valid = CardDropResult(changeTurn = true, valid = true, needsFine = false, nextStage = null)
        val missClick = CardDropResult(changeTurn = false, valid = false, needsFine = false, nextStage = null)
    }
}
