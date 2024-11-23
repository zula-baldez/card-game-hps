package com.example.common.kafkaconnections

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto

data class RoomUpdateEvent(
    val roomId: Long,
    val eventType: RoomUpdateEventType,
    val roomDto: RoomDto? = null,
    val newPlayer: AccountDto? = null,
    val playerLeave: PlayerLeaveEvent? = null
) {
    companion object {
        enum class RoomUpdateEventType {
            ROOM_CREATED, PLAYER_JOIN, PLAYER_LEAVE, ROOM_DELETED
        }

        data class PlayerLeaveEvent(
            val accountId: Long,
            val newHost: Long
        )
    }
}