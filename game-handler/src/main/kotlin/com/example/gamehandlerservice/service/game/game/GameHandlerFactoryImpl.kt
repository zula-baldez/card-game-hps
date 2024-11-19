package com.example.gamehandlerservice.service.game.game

import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Service

@Service
class GameHandlerFactoryImpl(
    private val beanFactoryGameHandler: ObjectFactory<GameHandler>
) : GameHandlerFactory {
    override fun instantiateGameHandler(roomId: Long): GameHandler {
        val gameHandler = beanFactoryGameHandler.getObject()
        gameHandler.setRoomId(roomId)
        return gameHandler
    }
}