package com.example.roomservice.service

import com.example.roomservice.repository.Room

interface RoomManager {
    fun createRoom(name: String, hostId: Long, capacity: Int): Room
    fun deleteRoom(id : Long)
    fun getRoom(id : Long) : Room?
    fun getAllRooms() : List<Room>
}
