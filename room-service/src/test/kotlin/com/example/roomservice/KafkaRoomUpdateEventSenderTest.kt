package com.example.roomservice

import com.example.common.kafkaconnections.RoomUpdateEvent
import com.example.roomservice.kafkaconnections.KafkaRoomUpdateEventSender
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.kafka.core.KafkaTemplate

class KafkaRoomUpdateEventSenderTest {

    private lateinit var kafkaTemplate: KafkaTemplate<String, RoomUpdateEvent>
    private lateinit var roomUpdateEventSender: KafkaRoomUpdateEventSender

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mock(KafkaTemplate::class.java) as KafkaTemplate<String, RoomUpdateEvent>
        roomUpdateEventSender = KafkaRoomUpdateEventSender(kafkaTemplate)
    }

    @Test
    fun `sendRoomUpdateEvent should send event to Kafka`() {
        val event = RoomUpdateEvent(1,RoomUpdateEvent.Companion.RoomUpdateEventType.ROOM_CREATED)

        roomUpdateEventSender.sendRoomUpdateEvent(event)

        verify(kafkaTemplate).send("game-connection-to-game-handler", event)
    }

    @Test
    fun `sendRoomUpdateEvent should not throw exception`() {
        val event = RoomUpdateEvent(1,RoomUpdateEvent.Companion.RoomUpdateEventType.ROOM_CREATED)

        assertDoesNotThrow {
            roomUpdateEventSender.sendRoomUpdateEvent(event)
        }
    }
}