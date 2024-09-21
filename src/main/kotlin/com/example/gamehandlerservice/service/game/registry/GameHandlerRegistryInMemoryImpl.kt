package com.example.gamehandlerservice.service.game.registry

import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.game.GameHandlerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class GameHandlerRegistryInMemoryImpl(
    private val factory: GameHandlerFactory,
    private val games: ConcurrentHashMap<Long, GameHandler> = ConcurrentHashMap()
) : GameHandlerRegistry {
    override fun createGame(name: String, roomId: Long): GameHandler {
        val game = factory.instantGameHandler(name, roomId)
        games[game.gameData.gameId] = game
        return game
    }

    override fun deleteGame(gameId: Long) {
        games.minus(gameId)
    }

    override fun getGame(gameId: Long): GameHandler? {
        return games[gameId]
    }
}