package com.example.gamehandlerservice.service.game.registry

import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.game.GameHandlerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class GameHandlerRegistryInMemoryImpl(
    private val factory: GameHandlerFactory,
    private val games: ConcurrentHashMap<Long, GameHandler> = ConcurrentHashMap()
) : GameHandlerRegistry {
    override fun createGame(room: RoomDto): GameHandler {
        val game = factory.instantiateGameHandler(room)
        games[room.id] = game
        return game
    }

    override fun deleteGame(roomId: Long) {
        games -= roomId
    }

    override fun getGame(roomId: Long): GameHandler? {
        return games[roomId]
    }
}