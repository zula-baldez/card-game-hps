package com.example.gamehandlerservice.service.game.game

interface GameHandlerFactory {
    fun instantGameHandler(name: String, roomId: Long) : GameHandler
}