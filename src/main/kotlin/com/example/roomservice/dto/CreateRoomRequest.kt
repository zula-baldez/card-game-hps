package com.example.roomservice.dto

data class CreateRoomRequest(val hostId: Long, val capacity: Int, val name: String)