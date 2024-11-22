package com.example.gamehandlerservice.kafkaconnections

import com.example.common.kafkaconnections.ConnectionMessage
import com.example.common.kafkaconnections.ConnectionMessageType
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ConnectionListener(
    private val registry: GameHandlerRegistry,
) {
    @KafkaListener(topics = ["game-connections"])
    fun listen(data: ConnectionMessage) {
        val game = registry.getGame(data.roomId)
        if (data.type == ConnectionMessageType.CONNECT) {
            game?.addPlayer(data.accountDto)
        } else if (data.type == ConnectionMessageType.DISCONNECT) {
            game?.kickPlayer(data.accountDto.id)
        }
    }
}