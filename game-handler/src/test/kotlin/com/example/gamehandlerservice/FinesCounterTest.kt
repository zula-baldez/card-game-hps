package com.example.gamehandlerservice

import com.example.common.client.PersonalAccountClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.CardDropResult
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.gamehandlerservice.service.game.util.FinesCounter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FinesCounterTest {

    private lateinit var finesCounter: FinesCounter
    private lateinit var personalAccountClient: PersonalAccountClient
    private lateinit var gameHandler: GameHandler
    private lateinit var accountFrom: AccountDto
    private lateinit var accountTo: AccountDto
    private lateinit var card: Card
    private lateinit var gameData: GameData

            @BeforeEach
    fun setUp() {
        personalAccountClient = mock(PersonalAccountClient::class.java)
        finesCounter = FinesCounter(personalAccountClient)
        gameData = GameData(
            gameId = 1L,
            roomId = 1L,
            trump = null,
            playersTurnQueue = CyclicQueue(
                listOf(
                    AccountDto(
                        name = "User1",
                        fines = 0,
                        id = 1L,
                        roomId = 1L
                    )
                )
            ),
            userCards = mutableMapOf(),
            finesCounter = mutableMapOf<Long, MutableMap<Long, Int>>()
        )
        gameHandler = mock(GameHandler::class.java)
        accountFrom = AccountDto(1, "fromAccountId", fines = 5, 1)
        accountTo = AccountDto(2, "toAccountId", fines = 2, 1)
        card = Card()
        `when`(gameHandler.gameData).thenReturn(gameData)

    }

    @Test
    fun `should return valid when fine is added successfully`() {
        val result = finesCounter.giveFine(gameHandler, accountFrom, accountTo, card)
        assertEquals(CardDropResult.valid, result)
        assertEquals(1, gameData.finesCounter[accountTo.id]?.get(accountFrom.id))
    }

    @Test
    fun `should return invalid when current fines are greater than or equal to accountTo fines`() {
        gameData.finesCounter[accountTo.id] = mutableMapOf(accountFrom.id to 2)

        val result = finesCounter.giveFine(gameHandler, accountFrom, accountTo, card)

        assertEquals(CardDropResult.invalid, result)
    }
}