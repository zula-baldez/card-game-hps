package com.example.gamehandlerservice.service.game.game

interface GameHandlerFactory {
    fun instantiateGameHandler(roomId: Long) : GameHandler
}