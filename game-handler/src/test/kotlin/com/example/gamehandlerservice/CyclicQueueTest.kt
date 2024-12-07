package com.example.gamehandlerservice

import com.example.gamehandlerservice.service.game.util.CyclicQueue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CyclicQueueTest {

    @Test
    fun `should initialize with provided items`() {
        val queue = CyclicQueue(listOf(1, 2, 3))
        assertEquals(3, queue.getSize())
        assertEquals(listOf(1, 2, 3), queue.getAll())
    }

    @Test
    fun `should return current item`() {
        val queue = CyclicQueue(listOf("a", "b", "c"))
        assertEquals("a", queue.current())
    }

    @Test
    fun `should return next item and rotate correctly`() {
        val queue = CyclicQueue(listOf("a", "b", "c"))
        assertEquals("a", queue.current())
        assertEquals("b", queue.next())
        queue.move(1)
        assertEquals("c", queue.next())
        queue.move(1)
        assertEquals("a", queue.next())
    }

    @Test
    fun `should return first item`() {
        val queue = CyclicQueue(listOf(1, 2, 3))
        assertEquals(1, queue.first())
    }


    @Test
    fun `should delete current item and adjust index correctly`() {
        val queue = CyclicQueue(listOf(1, 2, 3))
        assertEquals(1, queue.current())

        queue.delete(1)

        assertEquals(2, queue.getSize())
        assertEquals(listOf(2, 3), queue.getAll())
        assertEquals(2, queue.current())
    }

    @Test
    fun `should return null when deleting non-existent item`() {
        val queue = CyclicQueue(listOf(1, 2, 3))
        val result = queue.delete(4)

        assertNull(result)
        assertEquals(3, queue.getSize())
    }

    @Test
    fun `should reset current index to zero`() {
        val queue = CyclicQueue(listOf("a", "b", "c"))
        queue.next()
        queue.reset()

        assertEquals("a", queue.current())
    }

    @Test
    fun `should handle deletion when current index is at the last element`() {
        val queue = CyclicQueue(listOf(1, 2, 3))
        queue.next()
        queue.next()

        queue.delete(3)

        assertEquals(2, queue.getSize())
        assertEquals(listOf(1, 2), queue.getAll())
        assertEquals(1, queue.current())
    }

    @Test
    fun `should handle index change`() {
        val queue = CyclicQueue(listOf(1, 2, 3))
        queue.move(1)
        queue.move(1)

        queue.delete(2)

        assertEquals(2, queue.getSize())
        assertEquals(listOf(1, 3), queue.getAll())
        assertEquals(3, queue.current())
    }
}