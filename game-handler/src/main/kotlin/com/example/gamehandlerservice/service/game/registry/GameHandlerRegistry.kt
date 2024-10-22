package com.example.gamehandlerservice.service.game.registry

import com.example.gamehandlerservice.service.game.game.GameHandler

interface GameHandlerRegistry {
    fun createGame(name: String, roomId: Long): GameHandler
    fun deleteGame(gameId: Long)
    fun getGame(gameId: Long): GameHandler?
}