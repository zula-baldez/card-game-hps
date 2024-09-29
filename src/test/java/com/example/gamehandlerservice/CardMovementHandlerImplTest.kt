package com.example.gamehandlerservice

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.cards.CardMovementHandlerImpl
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.model.GameData
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.personalaccount.database.AccountEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.messaging.simp.SimpMessagingTemplate

class CardMovementHandlerImplTest {

    private lateinit var handler: CardMovementHandlerImpl
    private lateinit var simpMessagingTemplate: SimpMessagingTemplate
    private lateinit var gameHandler: GameHandler
    private lateinit var gameData: GameData
    private val roomId: Long = 1L
    private val gameId: Long = 100L


    @BeforeEach
    fun setUp() {
        simpMessagingTemplate = mock(SimpMessagingTemplate::class.java)
        handler = CardMovementHandlerImpl(simpMessagingTemplate)
        gameHandler = mock(GameHandler::class.java)
        gameData = GameData(
            gameId = gameId,
            roomId = roomId,
            trump = null,
            playersTurnQueue = CyclicQueue(
                listOf(
                    AccountEntity(
                        name = "User1",
                        fines = 0,
                        active = true,
                        id = 1L
                    )
                )
            ),
            userCards = mutableMapOf(),
            finesCounter = mutableMapOf()
        )
    }

    @Test
    fun `moveCard should move card from source to destination if present`() {
        val card = Card(Suit.Diamonds, 6L, false)
        val moveCardRequest = MoveCardRequest(fromDropArea = 1L, toDropArea = 2L, card = card)

        val sourceArea = LinkedHashSet<Card>().apply { add(card) }
        val destinationArea = LinkedHashSet<Card>()

        `when`(gameHandler.gameData).thenReturn(gameData)

        handler.moveCard(moveCardRequest, gameHandler)

        assert(!destinationArea.contains(card)) { "Card should be in the destination area" }
        assert(sourceArea.contains(card)) { "Card should be removed from the source area" }
    }

    @Test
    fun `moveCard should not move card if source does not contain it`() { val card = Card(Suit.Diamonds, 6L, false)
        val moveCardRequest = MoveCardRequest(fromDropArea = 1L, toDropArea = 2L, card = card)
        val destinationArea = LinkedHashSet<Card>()
        `when`(gameHandler.gameData).thenReturn(gameData)

        handler.moveCard(moveCardRequest, gameHandler)

        assert(destinationArea.isEmpty()) { "Destination area should remain empty" }
    }
}