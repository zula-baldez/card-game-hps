package com.example.gamehandlerservice.service.game.game

import com.example.common.client.RoomServiceClient
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class GameHandlerFactoryImpl(
    val roomServiceClient: RoomServiceClient,
    val simpMessagingTemplate: SimpMessagingTemplate
) : GameHandlerFactory {
    override fun instantiateGameHandler(roomId: Long): GameHandler {
        return GameHandlerImpl(roomServiceClient, simpMessagingTemplate, roomId)
    }
}