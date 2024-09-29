package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.game.AfterDropCardResult
import com.example.gamehandlerservice.model.game.AfterDropCardResult.Constants.nextStage
import com.example.gamehandlerservice.model.game.AfterDropCardResult.Constants.noStageChanges
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.cards.CardMovementHandler
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import org.springframework.stereotype.Component

@Component
class DistributionEventHandler(
    private val cardMovementHandler: CardMovementHandler
) : StageEventHandler {
    override var stage: Stage = Stage.DISTRIBUTION
    override fun afterDropCard(gameHandler: GameHandler): AfterDropCardResult {
        if (gameHandler.gameData.userCards[VirtualPlayers.TABLE.id]?.size == 0) {
            nextStage
        }
        return noStageChanges
    }

    override fun onStageEnd(gameHandler: GameHandler) {
    }

    override fun onStageStart(gameHandler: GameHandler) {
        cardMovementHandler.giveUsersBasicCards(gameHandler.gameData.playersTurnQueue.getAll(), gameHandler)
    }
}