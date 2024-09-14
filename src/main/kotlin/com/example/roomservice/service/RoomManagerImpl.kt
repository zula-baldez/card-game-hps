package com.example.roomservice.service

import com.example.gamehandlerservice.service.game.process.RoomHandler
import com.example.gamehandlerservice.service.game.process.RoomHandlerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


@Component
class RoomManagerImpl(
    private val roomHandlerFactory: RoomHandlerFactory
) : RoomManager {
    private val mp: MutableMap<Long, RoomHandler> = ConcurrentHashMap()

    override fun createRoom(name: String, hostId: Long, capacity: Int) : RoomHandler {
        val roomHandler: RoomHandler = roomHandlerFactory.instantGameHandler(name, hostId, capacity)
        mp[roomHandler.id] = roomHandler
        return roomHandler
    }

    override fun deleteRoom(id: Long) {
        mp.remove(id)
    }

    override fun getRoom(id: Long): RoomHandler? {
        return mp[id]
    }

    override fun getAllRooms(): List<RoomHandler> {
        return mp.map { it.value }.toList()
    }
}