package com.example.roomservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomRepo : JpaRepository<Room, Long>