package com.example.gamehandlerservice.service.game.process

interface RoomHandlerFactory {
    fun instantGameHandler(name: String, hostId: Long, capacity: Int) : RoomHandler
}