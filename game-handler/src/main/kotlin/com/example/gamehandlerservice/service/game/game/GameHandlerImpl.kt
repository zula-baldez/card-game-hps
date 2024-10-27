package com.example.gamehandlerservice.service.game.game

import com.example.common.client.RoomServiceClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.stage.StageStateMachineHandler
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class GameHandlerImpl(
    val roomServiceClient: RoomServiceClient
) : GameHandler {

    override lateinit var gameData: GameData
    override lateinit var stateMachine: StageStateMachineHandler
    private var timerJob: Job? = null

    override fun configureGameHandler(
        name: String,
        id: Long,
        roomId: Long,
        stateStageMachineHandler: StageStateMachineHandler
    ) {
        val room = roomServiceClient.findById(roomId)
        gameData = GameData(
            id,
            roomId,
            null,
            CyclicQueue(room.players.shuffled())
        )

        this.stateMachine = stateStageMachineHandler
    }

    override fun turningPlayer(): AccountDto = gameData.playersTurnQueue.current()

    override fun changeTurn() {
        gameData.playersTurnQueue.next()
    }

    override fun moveCard(moveCardRequest: MoveCardRequest) {
        restartTimer()
        stateMachine.processTurn(this, moveCardRequest)
    }

    override fun startGame() {
        val room = roomServiceClient.findById(gameData.roomId)
        gameData.playersTurnQueue = CyclicQueue(room.players.shuffled())
        stateMachine.nextStage(this)
        changeTurn()
        restartTimer()
    }

    override fun getStage(): Stage = stateMachine.stage

    private fun restartTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.IO).launch {
            delay(30000)
            timeOver()
        }
    }

    private fun timeOver() {
        changeTurn()
        restartTimer()
    }
}
