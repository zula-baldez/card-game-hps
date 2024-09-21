package com.example.gamehandlerservice.service.game.process.stage

import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.process.RoomHandler

interface StageEventHandler{
    var stage: Stage

    fun onStageEnd(roomHandler: RoomHandler)

    fun onStageStart(roomHandler: RoomHandler)
}