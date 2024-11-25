package com.example.gamehandlerservice

import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.game.GameHandlerFactory
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistryInMemoryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


class GameHandlerRegistryInMemoryImplTest {

    @Mock
    private lateinit var factory: GameHandlerFactory

    private lateinit var registry: GameHandlerRegistryInMemoryImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        registry = GameHandlerRegistryInMemoryImpl(factory)
    }

    @Test
    fun `createGame should create and store a game`() {
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val gameHandler: GameHandler = mock(GameHandler::class.java)

        `when`(factory.instantiateGameHandler(roomDto)).thenReturn(gameHandler)

        val createdGame = registry.createGame(roomDto)

        assertEquals(gameHandler, createdGame)
        assertEquals(gameHandler, registry.getGame(roomDto.id))
        verify(factory).instantiateGameHandler(roomDto)
    }

    @Test
    fun `deleteGame should remove a game`() {
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val gameHandler: GameHandler = mock(GameHandler::class.java)
        `when`(factory.instantiateGameHandler(roomDto)).thenReturn(gameHandler)

        registry.createGame(roomDto)

        registry.deleteGame(roomDto.id)

        assertNull(registry.getGame(roomDto.id))
    }

    @Test
    fun `getGame should return game if exists`() {
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val gameHandler: GameHandler = mock(GameHandler::class.java)
        `when`(factory.instantiateGameHandler(roomDto)).thenReturn(gameHandler)

        registry.createGame(roomDto)

        assertEquals(gameHandler, registry.getGame(roomDto.id))
    }

    @Test
    fun `getGame should return null if does not exist`() {
        assertNull(registry.getGame(999L))
    }
}