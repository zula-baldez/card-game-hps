package com.example.gamehandlerservice.service.game.registry

import com.example.gamehandlerservice.service.game.game.GameHandler

interface GameHandlerRegistry {
    fun createGame(roomId: Long): GameHandler
    fun deleteGame(roomId: Long)
    fun getGame(roomId: Long): GameHandler?
}