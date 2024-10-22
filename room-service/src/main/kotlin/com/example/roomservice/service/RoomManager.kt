package com.example.roomservice.service

import com.example.common.dto.api.ScrollPositionDto
import com.example.common.dto.business.RoomDto

interface RoomManager {
    fun createRoom(name: String, hostId: Long, capacity: Int): RoomDto
    fun deleteRoom(id : Long)
    fun getRoom(id : Long) : RoomDto?
    fun getAllRooms(scrollPosition: ScrollPositionDto? = null) : List<RoomDto>
}
