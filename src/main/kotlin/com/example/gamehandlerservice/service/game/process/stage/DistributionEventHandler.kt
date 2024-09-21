package com.example.gamehandlerservice.service.game.process.stage

import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.process.RoomHandler

class DistributionEventHandler : StageEventHandler {
    override var stage: Stage  = Stage.DISTRIBUTION
    override fun onStageEnd(roomHandler: RoomHandler) {
        TODO("Not yet implemented")
    }

    override fun onStageStart(roomHandler: RoomHandler) {
        TODO("Not yet implemented")
    }
}