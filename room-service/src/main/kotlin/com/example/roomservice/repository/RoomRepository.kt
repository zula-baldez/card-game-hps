package com.example.roomservice.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface RoomRepository : ReactiveSortingRepository<RoomEntity, Long>, ReactiveCrudRepository<RoomEntity, Long> {
    fun findAllByIdNotNull(pageable: Pageable): Flux<RoomEntity>
}