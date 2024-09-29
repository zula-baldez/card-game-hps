package com.example.gamehandlerservice.model.game

data class CardDropResult(
    val changeTurn: Boolean,
    val valid: Boolean,
    val needsFine: Boolean,
) {
    companion object Constants {
        val invalid = CardDropResult(changeTurn = true, valid = false, needsFine = true)
        val valid = CardDropResult(changeTurn = true, valid = true, needsFine = false)
        val missClick = CardDropResult(changeTurn = false, valid = false, needsFine = false)
    }
}
