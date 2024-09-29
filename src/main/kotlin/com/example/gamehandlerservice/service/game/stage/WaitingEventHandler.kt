package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.game.AfterDropCardResult
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.springframework.stereotype.Component

@Component
class WaitingEventHandler : StageEventHandler {
    override var stage: Stage = Stage.WAITING
    override fun afterDropCard(gameHandler: GameHandler): AfterDropCardResult = AfterDropCardResult.noStageChanges

    override fun onStageEnd(gameHandler: GameHandler) {
        //pass
    }

    override fun onStageStart(gameHandler: GameHandler) {
        gameHandler.gameData.userCards.clear()
    }
}