package com.example.roomservice.service

import com.example.common.kafkaconnections.RoomUpdateEvent

interface RoomUpdateEventSender {
    fun sendRoomUpdateEvent(event: RoomUpdateEvent)
}