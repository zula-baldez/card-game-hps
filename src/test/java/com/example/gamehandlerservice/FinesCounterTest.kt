package com.example.gamehandlerservice

import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.CardDropResult
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.gamehandlerservice.service.game.util.FinesCounter
import com.example.personalaccount.database.AccountEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*


class FinesCounterTest {

    private lateinit var finesCounter: FinesCounter

    @Mock
    private lateinit var gameHandler: GameHandler


    private val fromId = 1L
    private val toId = 2L
    private val initialFines = 2
    private lateinit var user: AccountEntity
    private lateinit var friend: AccountEntity
    private lateinit var gameData: GameData
    private val roomId: Long = 1L
    private val gameId: Long = 100L


    @BeforeEach
    fun setUp() {
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
        user = AccountEntity(
            name = "User1",
            fines = initialFines,
            id = fromId
        )
        friend = AccountEntity(
            name = "User2",
            fines = initialFines,
            id = toId
        )

        finesCounter = mock(FinesCounter::class.java)
        gameHandler = mock(GameHandler::class.java)

        `when`(gameHandler.gameData).thenReturn(gameData)
        finesCounter = FinesCounter()
    }

    @Test
    fun `giveFine should return valid when fines are less than target fines`() {
        val card = Card(Suit.Diamonds, 6L, false)
        val result = finesCounter.giveFine(gameHandler, user, friend, card)

        assertEquals(CardDropResult.valid, result)
        assertEquals(initialFines - 1, friend.fines)
    }

    @Test
    fun `giveFine should return invalid when current fines reach target fines`() {
        val card = Card(Suit.Diamonds, 6L, false)
        friend.fines = 0
        val result = finesCounter.giveFine(gameHandler, user, friend, card)

        assertEquals(CardDropResult.invalid, result)
        assertEquals(initialFines, user.fines)
    }
}