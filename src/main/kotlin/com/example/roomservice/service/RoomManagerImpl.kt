package com.example.roomservice.service

import com.example.gamehandlerservice.service.game.process.RoomHandlerFactoryImpl
import com.example.roomservice.repository.Room
import com.example.roomservice.repository.RoomRepo
import org.springframework.stereotype.Component

@Component
class RoomManagerImpl(
    private val roomRepo: RoomRepo,
    private val roomHandlerFactory: RoomHandlerFactoryImpl
) : RoomManager {
    override fun createRoom(name: String, hostId: Long, capacity: Int): Room {
        val room = Room(null, name, hostId, capacity, listOf())
        roomRepo.save(room)
        roomHandlerFactory.instantGameHandler(name, hostId, capacity) //TODO redis?
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