package com.example.roomservice.controllers

import com.example.roomservice.dto.CreateRoomDTO
import com.example.roomservice.dto.RoomChangeResponse
import com.example.roomservice.service.RoomManager
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class RoomController(
    private val roomManager: RoomManager
) {
    @GetMapping("/get-rooms")
    fun getAvailableRooms(): List<RoomChangeResponse> {
        return roomManager.getAllRooms().map { RoomChangeResponse(it.capacity, it.name, it.id?:0, it.hostId, it.players.size) }
            .toList()
    }

    @MessageMapping("/create-room")
    @SendTo("/topic/new-rooms")
    fun createRoom(createRoomDTO: CreateRoomDTO, principal: Principal): RoomChangeResponse {
        println("zalupa ${principal.name}")
        val roomHandler = roomManager.createRoom(
            createRoomDTO.name,
            principal.name.toLong(),
            createRoomDTO.capacity
        )
        return RoomChangeResponse(
            roomHandler.capacity,
            roomHandler.name,
            roomHandler.id ?: 0,
            roomHandler.hostId,
            roomHandler.players.size
        )
    }

    @MessageMapping("/all-rooms")
    @SendTo("/topic/all-rooms")
    fun getAllRooms(): List<RoomChangeResponse> {
        return roomManager.getAllRooms()
            .map { RoomChangeResponse(it.capacity, it.name, it.id ?: 0, it.hostId, it.players.size) }
            .toList()
    }
}