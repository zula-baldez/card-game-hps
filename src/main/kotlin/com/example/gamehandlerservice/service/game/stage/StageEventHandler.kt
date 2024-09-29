package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.game.AfterDropCardResult
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler

interface StageEventHandler {
    var stage: Stage

    fun afterDropCard(gameHandler: GameHandler): AfterDropCardResult

    fun onStageEnd(gameHandler: GameHandler)

    fun onStageStart(gameHandler: GameHandler)
}