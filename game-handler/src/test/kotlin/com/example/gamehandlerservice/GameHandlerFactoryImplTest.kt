package com.example.gamehandlerservice

import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.game.GameHandlerFactoryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.ObjectFactory

class GameHandlerFactoryImplTest {

    private lateinit var factory: GameHandlerFactoryImpl

    private lateinit var objectFactory: ObjectFactory<GameHandler>

    private lateinit var gameHandler: GameHandler

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        objectFactory = mock(ObjectFactory::class.java) as ObjectFactory<GameHandler>
        gameHandler = mock(GameHandler::class.java)
        factory = GameHandlerFactoryImpl(objectFactory)

        `when`(objectFactory.getObject()).thenReturn(gameHandler)
    }

    @Test
    fun `instantiateGameHandler should create GameHandler and set room`() {
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val result = factory.instantiateGameHandler(roomDto)

        verify(objectFactory).getObject()
        verify(gameHandler).setRoom(roomDto)
        assertEquals(gameHandler, result)
    }

    @Test
    fun `instantiateGameHandler should create different GameHandlers`() {
        val roomDto1 = RoomDto(
            id = 1L,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val roomDto2 = RoomDto(
            id = 2L,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val gameHandler1 = mock(GameHandler::class.java)
        val gameHandler2 = mock(GameHandler::class.java)
        `when`(objectFactory.getObject()).thenReturn(gameHandler1, gameHandler2)

        val result1 = factory.instantiateGameHandler(roomDto1)
        verify(gameHandler1).setRoom(roomDto1)

        val result2 = factory.instantiateGameHandler(roomDto2)
        verify(gameHandler2).setRoom(roomDto2)

        assertEquals(gameHandler1, result1)
        assertEquals(gameHandler2, result2)
    }
}