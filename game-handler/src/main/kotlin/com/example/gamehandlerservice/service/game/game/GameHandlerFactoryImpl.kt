package com.example.gamehandlerservice.service.game.game

import com.example.common.dto.roomservice.RoomDto
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Service

@Service
class GameHandlerFactoryImpl(
    private val beanFactoryGameHandler: ObjectFactory<GameHandler>
) : GameHandlerFactory {
    override fun instantiateGameHandler(room: RoomDto): GameHandler {
        val gameHandler = beanFactoryGameHandler.getObject()
        gameHandler.setRoom(room)
        return gameHandler
    }
}