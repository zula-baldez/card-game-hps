package com.example.gamehandlerservice.service.game.game

import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.stage.StageStateMachineHandler

interface GameHandler {
    var gameData: GameData
    var stateMachine: StageStateMachineHandler

    fun moveCard(moveCardRequest: MoveCardRequest)
    fun configureGameHandler(
        name: String,
        id: Long,
        roomId: Long,
        stateStageMachineHandler: StageStateMachineHandler
    )

    fun turningPlayer(): AccountDto?
    fun changeTurn()
    fun startGame()
    fun getStage(): Stage
}