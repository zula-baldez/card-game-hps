package com.example.kafkaconnections

import com.example.common.kafkaconnections.GameUpdateEvent
import com.example.gamehandlerservice.kafkaconnections.KafkaGameUpdateEventSender
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.fail
import org.mockito.Mockito.*
import org.springframework.kafka.core.KafkaTemplate

class KafkaGameUpdateEventSenderTest {

    private val kafkaTemplate: KafkaTemplate<String, GameUpdateEvent> =
        mock(KafkaTemplate::class.java) as KafkaTemplate<String, GameUpdateEvent>
    private val kafkaGameUpdateEventSender = KafkaGameUpdateEventSender(kafkaTemplate)

    @Test
    fun `test sendDisconnectEvent sends event to the correct topic`() {
        val event = GameUpdateEvent(
            1L,
            eventType = GameUpdateEvent.Companion.GameUpdateEventType.PLAYER_DISCONNECT,
            playerDisconnect = null
        )
        kafkaGameUpdateEventSender.sendDisconnectEvent(event)

        verify(kafkaTemplate).send("game-connection-to-room-service", event)
    }


    @Test
    fun `test sendDisconnectEvent handles exceptions`() {
        val event = GameUpdateEvent(
            1L,
            eventType = GameUpdateEvent.Companion.GameUpdateEventType.PLAYER_DISCONNECT,
            playerDisconnect = null
        )
        `when`(
            kafkaTemplate.send(
                anyString(),
                any(GameUpdateEvent::class.java)
            )
        ).thenThrow(RuntimeException("Kafka error"))

        try {
            kafkaGameUpdateEventSender.sendDisconnectEvent(event)
            fail("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            assertNotNull(e)
        }
    }
}