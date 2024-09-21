package com.example.gamehandlerservice.service.game.registry

import com.example.gamehandlerservice.service.game.game.GameHandler
import java.util.concurrent.ConcurrentHashMap

class GameHandlerRegistryInMemoryImpl(
    private val games: ConcurrentHashMap<Long, GameHandler> = ConcurrentHashMap(),
) : GameHandlerRegistry {
    override fun createGame(roomId: Long): GameHandler {
        TODO("Not yet implemented")
    }

    override fun deleteGame(gameId: Long) {
        games.minus(gameId)
    }

    override fun getGame(gameId: Long): GameHandler? {
        return games[gameId]
    }
}