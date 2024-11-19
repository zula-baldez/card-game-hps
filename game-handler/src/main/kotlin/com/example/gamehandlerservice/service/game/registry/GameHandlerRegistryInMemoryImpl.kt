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
    override fun createGame(roomId: Long): GameHandler {
        val game = factory.instantiateGameHandler(roomId)
        games[roomId] = game
        return game
    }

    override fun deleteGame(roomId: Long) {
        games -= roomId
    }

    override fun getGame(roomId: Long): GameHandler? {
        return games[roomId]
    }
}