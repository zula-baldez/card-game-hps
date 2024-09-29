package com.example.gamehandlerservice.model.game

data class AfterDropCardResult(
    val nextStage: Boolean
) {
    companion object Constants {
        val NEXT_STAGE = AfterDropCardResult(true)
        val NO_CHANGES = AfterDropCardResult(false)
    }

}