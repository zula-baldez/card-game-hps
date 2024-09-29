package com.example.roomservice.repository

import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Window
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomRepository : JpaRepository<RoomEntity, Long> {
    fun findFirst10ByOrderById(scrollPosition: ScrollPosition): Window<RoomEntity>
}