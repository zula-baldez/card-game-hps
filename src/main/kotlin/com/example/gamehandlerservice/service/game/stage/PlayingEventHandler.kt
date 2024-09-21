package com.example.gamehandlerservice.service.game.stage

import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.springframework.stereotype.Component

@Component
class PlayingEventHandler : StageEventHandler {
    override var stage: Stage = Stage.DISTRIBUTION
    override fun onStageEnd(gameHandler: GameHandler) {
        //pass
    }

    override fun onStageStart(gameHandler: GameHandler) {
        //pass
    }
}