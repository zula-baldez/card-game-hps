package com.example.gamehandlerservice.util.id.generator

import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong


@Component
class NonDatabaseEntityIdGenerator : IdGenerator {
    private val idCounter = AtomicLong(0)
    override fun generateRoomId(): Long {
        return idCounter.incrementAndGet()
    }

}