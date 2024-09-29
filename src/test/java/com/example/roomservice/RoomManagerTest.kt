package com.example.roomservice

import com.example.gamehandlerservice.service.game.cards.CardMovementHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import com.example.roomservice.repository.RoomEntity
import com.example.roomservice.repository.RoomRepository
import com.example.roomservice.service.RoomManagerImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*

class RoomManagerTest {

    @Mock
    private lateinit var roomRepository: RoomRepository

    @Mock
    private lateinit var cardMovementHandler: CardMovementHandler

    @Mock
    private lateinit var gameHandlerRegistry: GameHandlerRegistry

    @InjectMocks
    private lateinit var roomManager: RoomManagerImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }


    @Test
    fun `should delete room correctly`() {
        val roomId = 1L
        roomManager.deleteRoom(roomId)
        verify(roomRepository).deleteById(roomId)
    }

    @Test
    fun `should get room correctly if it exists`() {
        val roomId = 1L
        val room = RoomEntity(roomId, "Test Room", 1L, 10, 0, mutableListOf())
        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))

        val roomDto = roomManager.getRoom(roomId)
        assertEquals(roomId, roomDto?.id)
    }

    @Test
    fun `should return null if room does not exist`() {
        val roomId = 1L
        `when`(roomRepository.findById(roomId)).thenReturn(Optional.empty())

        val roomDto = roomManager.getRoom(roomId)
        assertEquals(null, roomDto)
    }

}

