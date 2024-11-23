package com.example.roomservice.kafkaconnections

import com.example.common.kafkaconnections.RoomUpdateEvent
import com.example.roomservice.service.RoomUpdateEventSender
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaRoomUpdateEventSender(private val kafkaTemplate: KafkaTemplate<String, RoomUpdateEvent>) : RoomUpdateEventSender {
    override fun sendRoomUpdateEvent(event: RoomUpdateEvent) {
        kafkaTemplate.send("game-connection-to-game-handler", event)
    }

}