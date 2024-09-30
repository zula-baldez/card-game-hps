package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.game.AfterDropCardResult
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.cards.CardMovementHandler
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import org.springframework.stereotype.Component

@Component
class PlayingEventHandler(
    private val cardMovementHandler: CardMovementHandler
) : StageEventHandler {
    override var stage: Stage = Stage.PLAYING
    override fun afterDropCard(gameHandler: GameHandler): AfterDropCardResult {
        if (gameHandler.gameData.userCards[VirtualPlayers.TABLE.id]?.size == gameHandler.gameData.playersTurnQueue.getSize()) {
            cardMovementHandler.clearPlayerCards(VirtualPlayers.TABLE.id, gameHandler)
        }
        gameHandler.gameData.userCards.forEach { playerCard ->
            if (playerCard.value.size == 0) {
                gameHandler.gameData.playersTurnQueue.getAll().first { player -> player.id == playerCard.key }
                    .let { gameHandler.gameData.playersTurnQueue.delete(it) }
            }
        }
        if (gameHandler.gameData.playersTurnQueue.getSize() == 1) {
            return AfterDropCardResult.nextStage
        }
        return AfterDropCardResult.noStageChanges
    }

    override fun onStageEnd(gameHandler: GameHandler) {
        //pass
    }

    override fun onStageStart(gameHandler: GameHandler) {
        //pass
    }
}