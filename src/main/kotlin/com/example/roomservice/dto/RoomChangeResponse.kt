package com.example.roomservice.dto

data class RoomChangeResponse(val capacity: Int, val name: String, val id: Long, val hostId: Long, val count: Long)