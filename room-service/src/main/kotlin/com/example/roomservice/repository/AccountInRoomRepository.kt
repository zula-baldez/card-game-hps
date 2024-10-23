package com.example.roomservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface AccountInRoomRepository : ReactiveCrudRepository<AccountInRoomEntity, Long> {
    fun findAllByRoomId(roomId: Long): Flux<AccountInRoomEntity>
}