package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler

interface StageStateMachineHandler {
    var stage: Stage

    fun processTurn(gameHandler: GameHandler, cardRequest: MoveCardRequest)

    fun nextStage(gameHandler: GameHandler)
}