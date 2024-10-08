package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.cards.CardMovementHandler
import com.example.gamehandlerservice.service.game.drop.DropStrategy
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.personalaccount.service.PersonalAccountManager
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class StageStateMachineHandlerImpl(
    dropHandlersList: List<DropStrategy>,
    eventHandlersList: List<StageEventHandler>,
    val accountManager: PersonalAccountManager,
    val cardHandler: CardMovementHandler
) : StageStateMachineHandler {
    override var stage: Stage = Stage.WAITING
        set(stage) {
            while (stateMachine.current() != stage) {
                stateMachine.next()
            }
            field = stage
        }
    private val stateMachine: CyclicQueue<Stage> = CyclicQueue(
        listOf(
            Stage.WAITING,
            Stage.DISTRIBUTION,
            Stage.FINES,
            Stage.PLAYING
        )
    )
    private val dropHandlers: Map<Stage, DropStrategy> = dropHandlersList.associateBy { it.stage }
    private val eventHandlers: Map<Stage, StageEventHandler> = eventHandlersList.associateBy { it.stage }

    override fun processTurn(gameHandler: GameHandler, cardRequest: MoveCardRequest) {
        val dropHandler = dropHandlers[stage] ?: throw NotImplementedError()
        val eventHandler = eventHandlers[stage] ?: throw NotImplementedError()

        val dropResult = dropHandler.validateDrop(cardRequest, gameHandler)
        if (dropResult.needsFine) {
            accountManager.addFine(gameHandler.gameData.playersTurnQueue.current().id)
        }
        if (dropResult.changeTurn) {
            gameHandler.changeTurn()
        }
        if (dropResult.valid) {
            cardHandler.moveCard(cardRequest, gameHandler)
        }
        val afterDropResult = eventHandler.afterDropCard(gameHandler)
        if (afterDropResult.nextStage) {
            nextStage(gameHandler)
        }
    }

    override fun nextStage(gameHandler: GameHandler) {
        eventHandlers[stage]?.onStageEnd(gameHandler)
        stage = stateMachine.next()
        eventHandlers[stage]?.onStageStart(gameHandler)
    }
}