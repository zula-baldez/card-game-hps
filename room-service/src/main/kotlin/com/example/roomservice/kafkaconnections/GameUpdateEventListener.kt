package com.example.roomservice.kafkaconnections

import com.example.common.dto.roomservice.AccountAction
import com.example.common.kafkaconnections.GameUpdateEvent
import com.example.common.kafkaconnections.GameUpdateEvent.Companion.GameUpdateEventType
import com.example.roomservice.service.RoomAccountManager
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class GameUpdateEventListener(
    private val roomAccountManager: RoomAccountManager
) {
    @KafkaListener(topics = ["game-connection-to-room-service"])
    fun listen(data: GameUpdateEvent) {
        if (data.eventType == GameUpdateEventType.PLAYER_DISCONNECT) {
            val event = data.playerDisconnect ?: throw IllegalArgumentException("broken message")
            roomAccountManager.removeAccount(data.roomId, event.accountId, AccountAction.BAN).block()
        }
    }
}
