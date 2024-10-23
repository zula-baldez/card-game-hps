package com.example.roomservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface BannedAccountInRoomRepository : ReactiveCrudRepository<BannedAccountInRoomEntity, Long> {
    fun findAllByRoomId(roomId: Long): Flux<BannedAccountInRoomEntity>
}