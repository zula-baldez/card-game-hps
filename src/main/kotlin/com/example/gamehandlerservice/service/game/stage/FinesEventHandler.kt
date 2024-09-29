package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.game.AfterDropCardResult
import com.example.gamehandlerservice.model.game.AfterDropCardResult.Constants.NEXT_STAGE
import com.example.gamehandlerservice.model.game.AfterDropCardResult.Constants.NO_CHANGES
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.springframework.stereotype.Component

@Component
class FinesEventHandler : StageEventHandler {
    override var stage: Stage = Stage.FINES
    override fun afterDropCard(gameHandler: GameHandler): AfterDropCardResult {
        val totalFines = gameHandler.gameData.finesCounter.values.flatMap { it.values }.sum()
        val totalPlayerFines = gameHandler.gameData.playersTurnQueue.getAll().sumOf { it.fines }

        if (totalFines ==
            totalPlayerFines
        ) {
            NEXT_STAGE
        }
        return NO_CHANGES
    }

    override fun onStageEnd(gameHandler: GameHandler) {
        //pass
    }

    override fun onStageStart(gameHandler: GameHandler) {
        //pass
    }
}