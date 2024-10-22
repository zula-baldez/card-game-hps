package com.example.gamehandlerservice

import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.CardCompareResult
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.personalaccount.database.AccountEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CardTest {

    private val userId = 1L
    private val friendId = 2L
    private val initialFines = 2
    private lateinit var user: AccountEntity
    private lateinit var friend: AccountEntity
    private lateinit var gameData: GameData
    private val gameId: Long = 100L
    private val roomId = 1L


    @BeforeEach
    fun setUp() {
        user = AccountEntity(
            name = "User1",
            fines = initialFines,
            id = userId
        )
        friend = AccountEntity(
            name = "User2",
            fines = initialFines,
            id = friendId
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

    }

    @Test
    fun `compareTo should return MORE when first card has greater strength and same suit`() {
        val card1 = Card(Suit.DIAMONDS, 10, false)
        val card2 = Card(Suit.DIAMONDS, 5, false)

        val result = card1.compareTo(gameData, card2)

        assertEquals(CardCompareResult.MORE, result)
    }

    @Test
    fun `compareTo should return LESS when second card has greater strength and same suit`() {
        val card1 = Card(Suit.CLUBS, 5, false)
        val card2 = Card(Suit.CLUBS, 10, false)

        val result = card1.compareTo(gameData, card2)

        assertEquals(CardCompareResult.LESS, result)
    }

    @Test
    fun `compareTo should return NOT_COMPARABLE when cards have different suits and neither is trump`() {
        val card1 = Card(Suit.DIAMONDS, 10, false)
        val card2 = Card(Suit.CLUBS, 5, false)

        val result = card1.compareTo(gameData, card2)

        assertEquals(CardCompareResult.NOT_COMPARABLE, result)
    }

    @Test
    fun `compareTo should return NOT_COMPARABLE when cards have same strength but different suits and neither is trump`() {
        val card1 = Card(Suit.SPADES, 10, false)
        val card2 = Card(Suit.CLUBS, 10, false)

        val result = card1.compareTo(gameData, card2)

        assertEquals(CardCompareResult.NOT_COMPARABLE, result)
    }
}