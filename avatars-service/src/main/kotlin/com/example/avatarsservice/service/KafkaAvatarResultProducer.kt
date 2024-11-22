package com.example.avatarsservice.service

import com.example.common.dto.Avatar
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaAvatarResultProducer(private val kafkaTemplate: KafkaTemplate<String, Avatar>) : AvatarResultProducer {
    override fun sendResult(avatar: Avatar) {
        kafkaTemplate.send("processed-avatars", avatar)
    }
}