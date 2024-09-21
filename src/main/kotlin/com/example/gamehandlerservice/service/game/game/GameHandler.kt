package com.example.gamehandlerservice.service.game.game

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.stage.StageStateMachineHandler
import com.example.personalaccount.database.Account

interface GameHandler {
    var gameData: GameData

    fun moveCard(moveCardRequest: MoveCardRequest)
    fun configureGameHandler(
        name: String,
        id: Long,
        roomId: Long,
        stateStageMachineHandler: StageStateMachineHandler
    )

    fun turningPlayer(): Account?
    fun changeTurn()
    fun startGame()
    fun getStage(): Stage
}