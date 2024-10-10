package com.example.gamehandlerservice

import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.game.GameHandlerFactory
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistryInMemoryImpl
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.personalaccount.database.AccountEntity
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class GameHandlerRegistryInMemoryTest {

    private lateinit var gameHandlerRegistry: GameHandlerRegistryInMemoryImpl

    @Mock
    private lateinit var factory: GameHandlerFactory

    @Mock
    private lateinit var gameHandler: GameHandler

    private val roomId: Long = 1L
    private val gameId: Long = 100L
    private val gameName = "Test Game"
    private lateinit var gameData: GameData

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        gameData = GameData(
            gameId = gameId,
            roomId = roomId,
            trump = null,
            playersTurnQueue = CyclicQueue(
                listOf(
                    AccountEntity(
                        name = "User1",
                        fines = 0,
                        id = 1L
                    )
                )
            ),
            userCards = mutableMapOf(),
            finesCounter = mutableMapOf()
        )
        gameHandlerRegistry = GameHandlerRegistryInMemoryImpl(factory)
        `when`(gameHandler.gameData).thenReturn(gameData)
        `when`(factory.instantGameHandler(gameName, roomId)).thenReturn(gameHandler)
    }

    @Test
    fun `createGame should create and store a game handler successfully`() {
        val createdGameHandler = gameHandlerRegistry.createGame(gameName, roomId)

        assertNotNull(createdGameHandler)
        assertNotNull(gameHandlerRegistry.getGame(gameId))
        verify(factory).instantGameHandler(gameName, roomId)
    }

    @Test
    fun `deleteGame should remove the game handler`() {
        gameHandlerRegistry.createGame(gameName, roomId)

        gameHandlerRegistry.deleteGame(gameId)

        assertNull(gameHandlerRegistry.getGame(gameId))
    }

    @Test
    fun `getGame should return null if the game does not exist`() {
        val result = gameHandlerRegistry.getGame(gameId)

        assertNull(result)
    }
}