package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.cards.CardMovementHandler
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.springframework.stereotype.Component

@Component
class DistributionEventHandler(
    private val cardMovementHandler: CardMovementHandler
) : StageEventHandler {
    override var stage: Stage  = Stage.DISTRIBUTION
    override fun onStageEnd(gameHandler: GameHandler) {

    }

    override fun onStageStart(gameHandler: GameHandler) {
        cardMovementHandler.giveUsersBasicCards(gameHandler.gameData.playersTurnQueue.getAll())
    }
}