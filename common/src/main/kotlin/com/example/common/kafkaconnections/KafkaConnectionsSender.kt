package com.example.common.kafkaconnections

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaConnectionsSender(private val kafkaTemplate: KafkaTemplate<String, ConnectionMessage>) {
    fun send(connectionMessage: ConnectionMessage) {
        kafkaTemplate.send("game-connections", connectionMessage)
    }
}
