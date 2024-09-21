package com.example.roomservice.service

import com.example.gamehandlerservice.service.game.game.GameHandlerFactory
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import com.example.roomservice.repository.Room
import com.example.roomservice.repository.RoomRepo
import org.springframework.stereotype.Component

@Component
class RoomManagerImpl(
    private val roomRepo: RoomRepo,
    private val gameHandlerFactory: GameHandlerFactory,
    private val gameHandlerRegistry: GameHandlerRegistry
) : RoomManager {
    override fun createRoom(name: String, hostId: Long, capacity: Int): Room {
        val room = Room(null, name, hostId, capacity, 0, listOf())
        roomRepo.save(room)
        val game = gameHandlerRegistry.createGame(name, room.id!!)
        room.currentGameId = game.gameData.gameId
        roomRepo.save(room)
        return room
    }

    override fun deleteRoom(id: Long) {
        roomRepo.deleteById(id)
    }

    override fun getRoom(id: Long): Room? {
        return roomRepo.findById(id).orElse(null)
    }

    override fun getAllRooms(): List<Room> {
        return roomRepo.findAll() //TODO pagination
    }
}