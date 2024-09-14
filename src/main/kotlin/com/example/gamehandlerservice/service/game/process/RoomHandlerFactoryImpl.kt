package com.example.gamehandlerservice.service.game.process

import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.util.id.generator.IdGenerator
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class RoomHandlerFactoryImpl(
    private val idGenerator: IdGenerator,
    private val beanFactory: ObjectFactory<RoomHandler>
) : RoomHandlerFactory {
    override fun instantGameHandler(name: String, hostId: Long, capacity: Int): RoomHandler {
        val id = idGenerator.generateRoomId()
        val roomHandler: RoomHandler = beanFactory.getObject()
        roomHandler.configureGameHandler(name, Stage.WAITING, id, hostId, capacity)
        return roomHandler
    }

}