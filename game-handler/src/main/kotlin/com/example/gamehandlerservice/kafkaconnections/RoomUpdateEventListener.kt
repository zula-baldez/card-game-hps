package com.example.gamehandlerservice.kafkaconnections

import com.example.common.kafkaconnections.RoomUpdateEvent
import com.example.common.kafkaconnections.RoomUpdateEvent.Companion.RoomUpdateEventType
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class RoomUpdateEventListener(
    private val registry: GameHandlerRegistry,
) {
    @KafkaListener(topics = ["game-connection-to-game-handler"])
    fun listen(data: RoomUpdateEvent) {
        if (data.eventType == RoomUpdateEventType.ROOM_CREATED) {
            registry.createGame(data.roomDto ?: throw IllegalArgumentException("No room dto in create room event for room ${data.roomId}"))
        } else if (data.eventType == RoomUpdateEventType.ROOM_DELETED) {
            registry.deleteGame(data.roomId)
        } else if (data.eventType == RoomUpdateEventType.PLAYER_JOIN) {
            val room = registry.getGame(data.roomId) ?: throw IllegalArgumentException("Room ${data.roomId} not found for player join")
            room.addPlayer(data.newPlayer ?: throw IllegalArgumentException("No account dto in player join event for room ${data.roomId}"))
        } else if (data.eventType == RoomUpdateEventType.PLAYER_LEAVE) {
            val room = registry.getGame(data.roomId) ?: throw IllegalArgumentException("Room ${data.roomId} not found for player leave")
            val playerLeave = data.playerLeave ?: throw IllegalArgumentException("No player leave in player leave event for room ${data.roomId}")
            room.removePlayer(playerLeave.accountId)
            room.updateHostId(playerLeave.newHost)
        }

    }
}