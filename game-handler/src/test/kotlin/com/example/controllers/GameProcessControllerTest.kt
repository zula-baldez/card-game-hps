package com.example.controllers

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.controllers.GameProcessController
import com.example.gamehandlerservice.model.dto.*
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.messaging.simp.SimpMessageSendingOperations

class GameProcessControllerTest {

    @Mock
    private lateinit var registry: GameHandlerRegistry

    @Mock
    private lateinit var messagingTemplate: SimpMessageSendingOperations

    private lateinit var controller: GameProcessController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        controller = GameProcessController(registry, messagingTemplate)
    }

    @Test
    fun `test moveCard should call handle on game object`() {
        val accountDto = AccountDto(id = 2L, name = "Player1", fines = 0, "avatar", roomId = 1L)
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val playerActionRequest = PlayerActionRequest(2L, action = PlayerAction.DROP_CARD, droppedCard = Card())

        val gameMock = mock(GameHandler::class.java)
        `when`(registry.getGame(roomDto.id)).thenReturn(gameMock)

        controller.moveCard(accountDto, roomDto, playerActionRequest)

        verify(gameMock).handle(playerActionRequest.copy(playerId = accountDto.id))
    }

    @Test
    fun `test getFullState should send full game state`() {
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val gameMock = mock(GameHandler::class.java)
        val gameStateMock = GameStateResponse(listOf(), GameState(1L, 2L, true), Card(suit = Suit.SPADES), 10,GameStage.STARTED, null, emptyList(), emptyMap(), 1L)

        `when`(registry.getGame(roomDto.id)).thenReturn(gameMock)
        `when`(gameMock.getGameState()).thenReturn(gameStateMock)

        controller.getFullState(roomDto)

        verify(messagingTemplate).convertAndSend("/topic/room/${roomDto.id}/events", gameStateMock)
    }

    @Test
    fun `test startGame should call startGame on game object`() {
        val accountDto = AccountDto(id = 2L, name = "Player1", fines = 0, "avatar", roomId = 1L)
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )

        val gameMock = mock(GameHandler::class.java)
        `when`(registry.getGame(roomDto.id)).thenReturn(gameMock)

        controller.startGame(accountDto, roomDto)

        verify(gameMock).startGame()
    }

    @Test
    fun `test getFullState should throw exception if game not found`() {
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        `when`(registry.getGame(roomDto.id)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            controller.getFullState(roomDto)
        }
        assertEquals("Game ${roomDto.id} not found in registry", exception.message)
    }
}