package com.example.gamehandlerservice

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.CardDropResult
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.drop.DistributionDropStrategy
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import com.example.personalaccount.database.AccountEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class DropStrategyTests {

    private lateinit var dropStrategy: DistributionDropStrategy
    private lateinit var gameHandler: GameHandler
    private lateinit var request: MoveCardRequest
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
        dropStrategy = DistributionDropStrategy()
        gameHandler = Mockito.mock(GameHandler::class.java)
        request = MoveCardRequest(
            fromDropArea = VirtualPlayers.DECK.id,
            toDropArea = VirtualPlayers.TABLE.id,
            card = Card(Suit.Diamonds, 6L, false)
        )
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
    fun `test validateDrop with invalid fromDropArea`() {
        request.fromDropArea = 1L

        `when`(gameHandler.gameData).thenReturn(gameData)
        val result = dropStrategy.validateDrop(request, gameHandler)

        assertEquals(CardDropResult.invalid, result)
    }

    @Test
    fun `test validateDrop onDropTable flow`() {
        request.fromDropArea = VirtualPlayers.TABLE.id
        `when`(gameHandler.gameData).thenReturn(gameData)
        val result = dropStrategy.validateDrop(request, gameHandler)

        assertEquals(CardDropResult(changeTurn = false, valid = false, needsFine = false), result)
    }

    @Test
    fun `test validateDrop onDropEnemy flow`() {
        request.fromDropArea = VirtualPlayers.DECK.id
        `when`(gameHandler.gameData).thenReturn(gameData)
        request.toDropArea = friend.id

        val result = dropStrategy.validateDrop(request, gameHandler)

        assertEquals(CardDropResult(changeTurn=true, valid=false, needsFine=true), result)
    }
}