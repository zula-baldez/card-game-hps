package com.example.gamehandlerservice.model.game

data class AfterDropCardResult(
    val nextStage: Boolean
) {
    companion object Constants {
        val nextStage = AfterDropCardResult(true)
        val noStageChanges = AfterDropCardResult(false)
    }

}