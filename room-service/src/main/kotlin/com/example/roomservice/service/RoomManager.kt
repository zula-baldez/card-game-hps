package com.example.roomservice.service

import com.example.common.dto.api.Pagination
import com.example.common.dto.roomservice.RoomDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoomManager {
    fun createRoom(name: String, hostId: Long, capacity: Int): Mono<RoomDto>
    fun deleteRoom(id : Long): Mono<Void>
    fun getRoom(id : Long): Mono<RoomDto>
    fun getRooms(page: Pagination): Flux<RoomDto>
}
