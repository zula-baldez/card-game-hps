package com.example.gamehandlerservice.service.game.game

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.stage.StageStateMachineHandler
import com.example.personalaccount.database.AccountEntity

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

    fun turningPlayer(): AccountEntity?
    fun changeTurn()
    fun startGame()
    fun getStage(): Stage
}