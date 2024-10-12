package com.example.roomservice

import com.example.common.dto.api.ScrollPositionDto
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
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Window
import java.util.*
import java.util.function.IntFunction

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

    @Test
    fun `should return a list of rooms when called with a valid scroll position`() {
        val scrollPosition = ScrollPositionDto(offset = 10)
        val roomEntities = listOf(
            RoomEntity(1L, "Test Room", 1L, 10, 0, mutableListOf()),
            RoomEntity(2L, "Test Room 2", 1L, 10, 0, mutableListOf())
        )
        val intFunc =
            IntFunction { ScrollPosition.offset(scrollPosition.offset ?: 0) }
        val window = Window.from(roomEntities, intFunc)
        `when`(roomRepository.findFirst50ByOrderById(ScrollPosition.offset(10))).thenReturn(window)

        val result = roomManager.getAllRooms(scrollPosition)

        assertEquals(2, result.size)
        assertEquals("Test Room", result[0].name)
        assertEquals("Test Room 2", result[1].name)
        verify(roomRepository).findFirst50ByOrderById(ScrollPosition.offset(10))
    }

}

