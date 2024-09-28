package com.example.roomservice.service

import com.example.common.dto.api.ScrollPositionDto
import com.example.gamehandlerservice.service.game.game.GameHandlerFactory
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import com.example.common.dto.business.RoomDto
import com.example.roomservice.repository.Room
import com.example.roomservice.repository.RoomRepository
import org.springframework.data.domain.ScrollPosition
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class RoomManagerImpl(
    private val roomRepository: RoomRepository,
    private val gameHandlerRegistry: GameHandlerRegistry
) : RoomManager {
    override fun createRoom(name: String, hostId: Long, capacity: Int): RoomDto {
        val room = Room(0, name, hostId, capacity, 0, mutableListOf())
        roomRepository.save(room)
        val game = gameHandlerRegistry.createGame(name, room.id)
        room.currentGameId = game.gameData.gameId
        roomRepository.save(room)
        return room.toDto()
    }

    override fun deleteRoom(id: Long) {
        roomRepository.deleteById(id)
    }

    override fun getRoom(id: Long): RoomDto? {
        return roomRepository.findById(id).map { it.toDto() }.getOrNull()
    }

    override fun getAllRooms(scrollPosition: ScrollPositionDto?): List<RoomDto> {
        return roomRepository
            .findFirst10ByOrderById(ScrollPosition.offset(scrollPosition?.offset ?: 0))
            .map { it.toDto() }
            .toList()
    }
}