package com.example.common.kafkaconnections

data class GameUpdateEvent(
    val roomId: Long,
    val eventType: GameUpdateEventType,
    val playerDisconnect: PlayerDisconnectGameUpdateEvent?
) {
    companion object {
        enum class GameUpdateEventType {
            PLAYER_DISCONNECT
        }

        data class PlayerDisconnectGameUpdateEvent(
            val accountId: Long
        )
    }
}