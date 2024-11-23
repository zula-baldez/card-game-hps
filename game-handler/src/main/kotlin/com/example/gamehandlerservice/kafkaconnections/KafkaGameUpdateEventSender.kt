package com.example.gamehandlerservice.kafkaconnections

import com.example.common.kafkaconnections.GameUpdateEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaGameUpdateEventSender(private val template: KafkaTemplate<String, GameUpdateEvent>) {
    fun sendDisconnectEvent(event: GameUpdateEvent) {
        template.send("game-connection-to-room-service", event)
    }
}