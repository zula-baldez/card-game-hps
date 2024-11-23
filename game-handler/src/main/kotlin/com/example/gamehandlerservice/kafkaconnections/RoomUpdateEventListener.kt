package com.example.gamehandlerservice.kafkaconnections

import com.example.common.kafkaconnections.RoomUpdateEvent
import com.example.common.kafkaconnections.RoomUpdateEvent.Companion.RoomUpdateEventType
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class RoomUpdateEventListener(
    private val registry: GameHandlerRegistry,
) {
    private val logger = LoggerFactory.getLogger(RoomUpdateEventListener::class.java)

    @KafkaListener(topics = ["game-connection-to-game-handler"])
    fun listen(data: RoomUpdateEvent) {
        when (data.eventType) {
            RoomUpdateEventType.ROOM_CREATED -> {
                logger.info("Received room create event")
                registry.createGame(
                    data.roomDto
                        ?: throw IllegalArgumentException("No room dto in create room event for room ${data.roomId}")
                )
            }

            RoomUpdateEventType.ROOM_DELETED -> {
                logger.info("Received room delete event")
                registry.deleteGame(data.roomId)
            }

            RoomUpdateEventType.PLAYER_JOIN -> {
                logger.info("Received player join event")
                val room = registry.getGame(data.roomId)
                    ?: throw IllegalArgumentException("Room ${data.roomId} not found for player join")
                room.addPlayer(
                    data.newPlayer
                        ?: throw IllegalArgumentException("No account dto in player join event for room ${data.roomId}")
                )
            }

            RoomUpdateEventType.PLAYER_LEAVE -> {
                logger.info("Received player leave event")
                val room = registry.getGame(data.roomId)
                    ?: throw IllegalArgumentException("Room ${data.roomId} not found for player leave")
                val playerLeave = data.playerLeave
                    ?: throw IllegalArgumentException("No player leave in player leave event for room ${data.roomId}")
                room.removePlayer(playerLeave.accountId)
                room.updateHostId(playerLeave.newHost)
            }
        }

    }
}