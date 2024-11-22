package com.example.common.kafkaconnections

import org.springframework.context.annotation.Lazy
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
@Lazy
class KafkaConnectionsSender(private val kafkaTemplate: KafkaTemplate<String, ConnectionMessage>) {
    fun send(topic: String, connectionMessage: ConnectionMessage) {
        kafkaTemplate.send(topic, connectionMessage)
    }
}
