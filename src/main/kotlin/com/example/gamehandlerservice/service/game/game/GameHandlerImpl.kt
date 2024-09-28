package com.example.gamehandlerservice.service.game.game

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.cards.CardMovementHandler
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.stage.StageStateMachineHandler
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.personalaccount.database.Account
import com.example.roomservice.repository.Room
import com.example.roomservice.repository.RoomRepository
import com.example.roomservice.service.RoomManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
@Scope("prototype")
class GameHandlerImpl(
    val cardMovementHandler: CardMovementHandler,
    val roomManager: RoomManager,
    val roomRepository: RoomRepository
) : GameHandler {

    override lateinit var gameData: GameData
    private var timerJob: Job? = null
    private lateinit var stateStageMachineHandler: StageStateMachineHandler

    val room: Room
        get() = roomRepository.findById(gameData.roomId).getOrNull() ?: throw IllegalArgumentException()

    override fun configureGameHandler(
        name: String,
        id: Long,
        roomId: Long,
        stateStageMachineHandler: StageStateMachineHandler
    ) {
        val players = CyclicQueue(room.players.shuffled())
        gameData = GameData(
            id,
            roomId,
            null,
            players
        )
        cardMovementHandler.giveUsersBasicCards(room.players)
        this.stateStageMachineHandler = stateStageMachineHandler
    }

    override fun turningPlayer(): Account = gameData.playersTurnQueue.current()

    override fun changeTurn() {
        gameData.playersTurnQueue.next()
    }

    override fun moveCard(moveCardRequest: MoveCardRequest) {
        restartTimer()
        stateStageMachineHandler.processTurn(this, moveCardRequest)
    }

    override fun startGame() {
        stateStageMachineHandler.nextStage(this)
        changeTurn()
        restartTimer()
    }

    override fun getStage(): Stage = stateStageMachineHandler.stage

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
