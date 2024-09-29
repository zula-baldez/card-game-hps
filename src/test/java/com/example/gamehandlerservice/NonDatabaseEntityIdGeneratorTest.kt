package com.example.gamehandlerservice

import com.example.gamehandlerservice.util.id.generator.NonDatabaseEntityIdGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NonDatabaseEntityIdGeneratorTest {

    private val idGenerator = NonDatabaseEntityIdGenerator()

    @Test
    fun `should generate unique IDs sequentially`() {
        val firstId = idGenerator.generateId()
        val secondId = idGenerator.generateId()

        assertEquals(1, firstId)
        assertEquals(2, secondId)
    }

    @Test
    fun `should continue ID generation without reset`() {
        val idList = mutableListOf<Long>()
        repeat(100) {
            idList.add(idGenerator.generateId())
        }
        assertEquals(1L, idList.first())
        assertEquals(100L, idList.last())
        assertEquals(100, idList.toSet().size)
    }
}