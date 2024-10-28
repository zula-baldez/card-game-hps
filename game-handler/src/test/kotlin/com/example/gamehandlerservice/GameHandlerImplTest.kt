package com.example.gamehandlerservice

import com.example.common.client.RoomServiceClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.cards.CardMovementHandler
import com.example.gamehandlerservice.service.game.game.GameHandlerImpl
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.stage.StageStateMachineHandler
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*


class GameHandlerImplTest {

    private lateinit var gameHandler: GameHandlerImpl
    private val cardMovementHandler: CardMovementHandler = mock(CardMovementHandler::class.java)

    private val roomRepository: RoomServiceClient = mock(RoomServiceClient::class.java)
    private val stateStageMachineHandler: StageStateMachineHandler = mock(StageStateMachineHandler::class.java)

    private val userId = 1L
    private val friendId = 2L
    private val initialFines = 2
    private lateinit var user: AccountDto
    private lateinit var friend: AccountDto
    private lateinit var gameData: GameData
    private val gameId: Long = 100L
    private val roomId = 1L


    @BeforeEach
    fun setUp() {
        user = AccountDto(
            name = "User1",
            fines = initialFines,
            id = userId, roomId = 1
        )
        friend = AccountDto(
            name = "User2",
            fines = initialFines,
            id = friendId, roomId = 1
        )
        gameData = GameData(
            gameId = gameId,
            roomId = roomId,
            trump = null,
            playersTurnQueue = CyclicQueue(
                listOf(
                    user, friend
                )
            ),
            userCards = mutableMapOf(),
            finesCounter = mutableMapOf()
        )

        gameHandler = GameHandlerImpl(roomRepository)
    }

    @Test
    fun `configureGameHandler should initialize gameData correctly`() {
        val players = mutableListOf(user, friend)
        val roomEntity = RoomDto(roomId, "Test Room", 1L, 10, players, 1L, listOf())

        `when`(roomRepository.findById(roomId)).thenReturn(roomEntity)

        gameHandler.gameData = gameData
        gameHandler.configureGameHandler("TestGame", 1L, roomEntity.id, stateStageMachineHandler)

        assertEquals(roomEntity.id, gameHandler.gameData.roomId)
    }

    @Test
    fun `turningPlayer should return the current player in turn`() {
        val players = mutableListOf(user, friend)
        val roomEntity = RoomDto(roomId, "Test Room", 1L, 10, players, 1L, listOf())

        `when`(roomRepository.findById(roomId)).thenReturn(roomEntity)
        gameHandler.gameData = gameData
        gameHandler.configureGameHandler("TestGame", 1L, roomId, stateStageMachineHandler)
        val shuffledPlayers = gameHandler.gameData.playersTurnQueue.getAll()
        assertEquals(shuffledPlayers[0].name, gameHandler.turningPlayer().name)
    }

    @Test
    fun `changeTurn should change the current player in turn`() {
        val players = mutableListOf(user, friend)
        val roomEntity = RoomDto(roomId, "Test Room", 1L, 10, players, 1L, listOf())

        `when`(roomRepository.findById(roomId)).thenReturn(roomEntity)
        gameHandler.gameData = gameData
        gameHandler.configureGameHandler("TestGame", 1L, roomId, stateStageMachineHandler)

        gameHandler.changeTurn()
        val shuffledPlayers = gameHandler.gameData.playersTurnQueue.getAll()
        assertEquals(shuffledPlayers[1].name, gameHandler.turningPlayer().name)
    }

    @Test
    fun `moveCard should call processTurn on stateStageMachineHandler`() {
        val card = Card(Suit.DIAMONDS, 6L, false)
        val moveCardRequest = MoveCardRequest(fromDropArea = 1L, toDropArea = 2L, card = card)
        val players = mutableListOf(user, friend)
        val roomEntity = RoomDto(roomId, "Test Room", 1L, 10, players, 1L, listOf())

        `when`(roomRepository.findById(roomId)).thenReturn(roomEntity)
        gameHandler.gameData = gameData
        gameHandler.configureGameHandler("TestGame", 1L, roomId, stateStageMachineHandler)

        gameHandler.moveCard(moveCardRequest)

        verify(stateStageMachineHandler).processTurn(gameHandler, moveCardRequest)
    }

    @Test
    fun `startGame should initialize the game and change turn`() {
        val players = mutableListOf(user, friend)
        val roomEntity = RoomDto(roomId, "Test Room", 1L, 10, players, 1L, listOf())

        `when`(roomRepository.findById(roomId)).thenReturn(roomEntity)
        gameHandler.gameData = gameData
        gameHandler.configureGameHandler("TestGame", 1L, roomId, stateStageMachineHandler)

        gameHandler.startGame()

        verify(stateStageMachineHandler).nextStage(gameHandler)
        val shuffledPlayers = gameHandler.gameData.playersTurnQueue.getAll()
        assertEquals(shuffledPlayers[1].name, gameHandler.turningPlayer().name)
    }

    @Test
    fun testGetStage() {
        val players = mutableListOf(user, friend)
        val roomEntity = RoomDto(roomId, "Test Room", 1L, 10, players, 1L, listOf())

        `when`(roomRepository.findById(roomId)).thenReturn(roomEntity)
        gameHandler.gameData = gameData
        gameHandler.configureGameHandler("TestGame", 1L, roomId, stateStageMachineHandler)

        assertEquals(stateStageMachineHandler.stage, gameHandler.getStage())
    }

}