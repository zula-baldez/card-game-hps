package com.example.gamehandlerservice.service.game.registry

import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.service.game.game.GameHandler

interface GameHandlerRegistry {
    fun createGame(room: RoomDto): GameHandler
    fun deleteGame(roomId: Long)
    fun getGame(roomId: Long): GameHandler?
}