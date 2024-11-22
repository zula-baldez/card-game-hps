package com.example

import com.example.avatarsservice.service.KafkaAvatarResultProducer
import com.example.common.dto.Avatar
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.kafka.core.KafkaTemplate

class KafkaAvatarResultProducerTest {

    private val kafkaTemplate: KafkaTemplate<String, Avatar> =
        mock(KafkaTemplate::class.java) as KafkaTemplate<String, Avatar>
    private val producer = KafkaAvatarResultProducer(kafkaTemplate)

    @Test
    fun `should send avatar to kafka topic`() {
        val avatar = Avatar(1L, "/test")
        producer.sendResult(avatar)
        verify(kafkaTemplate).send("processed-avatars", avatar)
    }
}