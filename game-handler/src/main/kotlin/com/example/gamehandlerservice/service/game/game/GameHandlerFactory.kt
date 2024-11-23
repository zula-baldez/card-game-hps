package com.example.gamehandlerservice.service.game.game

import com.example.common.dto.roomservice.RoomDto

interface GameHandlerFactory {
    fun instantiateGameHandler(room: RoomDto) : GameHandler
}