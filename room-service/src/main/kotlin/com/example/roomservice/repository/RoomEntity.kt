package com.example.roomservice.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "room")
data class RoomEntity(
    @Id
    val id: Long,
    val name: String,
    val hostId: Long,
    val capacity: Int,
    var currentGameId: Long
)