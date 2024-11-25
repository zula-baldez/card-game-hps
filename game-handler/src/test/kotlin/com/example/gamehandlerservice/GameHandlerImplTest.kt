package com.example.gamehandlerservice

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.exceptions.GameException
import com.example.gamehandlerservice.model.dto.GameStage
import com.example.gamehandlerservice.model.dto.GameState
import com.example.gamehandlerservice.model.dto.PlayerAction
import com.example.gamehandlerservice.model.dto.PlayerActionRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.game.GameHandlerImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.verify
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.util.ReflectionTestUtils


class GameHandlerImplTest {

    private lateinit var gameHandler: GameHandlerImpl
    private lateinit var simpMessagingTemplate: SimpMessagingTemplate
    private lateinit var accountDto: AccountDto
    private lateinit var accountDto2: AccountDto
    private lateinit var roomDto: RoomDto


    @BeforeEach
    fun setUp() {
        simpMessagingTemplate = mock(SimpMessagingTemplate::class.java)
        gameHandler = GameHandlerImpl(simpMessagingTemplate)
        accountDto = AccountDto(id = 1L, name = "Player1", fines = 0, "avatar", roomId = 1L)
        accountDto2 = AccountDto(id = 2L, name = "Player2", fines = 0, "avatar2", roomId = 1L)
        roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )

    }

    @Test
    fun `test setRoom updates room properties`() {

        gameHandler.setRoom(roomDto)

        val roomIdField = GameHandlerImpl::class.java.getDeclaredField("roomId").apply { isAccessible = true }
        val roomNameField = GameHandlerImpl::class.java.getDeclaredField("roomName").apply { isAccessible = true }
        val roomHostField = GameHandlerImpl::class.java.getDeclaredField("roomHost").apply { isAccessible = true }

        assertEquals(0L, roomIdField.get(gameHandler))
        assertEquals("Комната для игры", roomNameField.get(gameHandler))
        assertEquals(1L, roomHostField.get(gameHandler))
    }

    @Test
    fun `test updateHostId updates the room host id`() {
        gameHandler.setRoom(roomDto)

        gameHandler.updateHostId(200)
        val roomHost = GameHandlerImpl::class.java.getDeclaredField("roomHost").apply { isAccessible = true }

        assertEquals(200L, roomHost.get(gameHandler))
    }


    @Test
    fun `startGame should initialize game correctly if not started`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)


        gameHandler.startGame()

        val stage = GameHandlerImpl::class.java.getDeclaredField("stage").apply { isAccessible = true }
        assertEquals(GameStage.STARTED, stage.get(gameHandler))

    }

    @Test
    fun `startGame should throw GameException if already started`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)

        gameHandler.startGame()

        val exception = assertThrows<GameException> {
            gameHandler.startGame()
        }
        assertEquals("Game already started", exception.message)
    }

    @Test
    fun `removePlayer should remove defend player and clear table`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)

        gameHandler.removePlayer(1L)
        val playersField = GameHandlerImpl::class.java.getDeclaredField("players").apply { isAccessible = true }
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }

        val players = playersField.get(gameHandler) as MutableList<AccountDto>
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>

        assertFalse { players.any { it.id == 1L } }
        assertFalse { playersCards.contains(1L) }

    }

    @Test
    fun `removePlayer should remove attack player and clear table`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)

        gameHandler.removePlayer(2L)
        val playersField = GameHandlerImpl::class.java.getDeclaredField("players").apply { isAccessible = true }
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }

        val players = playersField.get(gameHandler) as MutableList<AccountDto>
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>

        assertFalse { players.any { it.id == 2L } }
        assertFalse { playersCards.contains(2L) }
    }

    @Test
    fun `removePlayer should handle player not in the game`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)

        gameHandler.removePlayer(99L)

    }


    @Test
    fun `removePlayer should handle players who are not defender or attacker`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)

        gameHandler.removePlayer(3L)

        val playersField = GameHandlerImpl::class.java.getDeclaredField("players").apply { isAccessible = true }
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }

        val players = playersField.get(gameHandler) as MutableList<AccountDto>
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>

        assertFalse { players.any { it.id == 3L } }
        assertFalse { playersCards.contains(3L) }

        assertEquals(2, players.size)

    }

    @Test
    fun `should throw exception when game hasn't started`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)

        val stage = GameHandlerImpl::class.java.getDeclaredField("stage").apply { isAccessible = true }
        assertEquals(GameStage.WAITING, stage.get(gameHandler))
        val exception = assertThrows<GameException> {
            gameHandler.handle(playerAction = PlayerActionRequest(1L, Card(Suit.SPADES), PlayerAction.DROP_CARD))
        }

        assertEquals("Game hasn't started", exception.message)
    }

    @Test
    fun `should throw GameException when attacker dont drop card`() {
        val player1 = accountDto
        gameHandler.addPlayer(player1)
        gameHandler.startGame()

        val exception = assertThrows<GameException> {
            gameHandler.handle(PlayerActionRequest(player1.id, null, action = PlayerAction.DROP_CARD))
        }
        assertEquals("No card in request", exception.message)
    }

    @Test
    fun `should throw GameException when attacker has no cards`() {
        val player1 = accountDto
        gameHandler.addPlayer(player1)
        gameHandler.startGame()

        val exception = assertThrows<GameException> {
            gameHandler.handle(PlayerActionRequest(player1.id, Card(Suit.SPADES, 5), action = PlayerAction.DROP_CARD))
        }
        assertEquals("No card in hand!", exception.message)
    }


    @Test
    fun `should throw GameException when attacker drop invalid card`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)
        gameHandler.startGame()

        val stateField = GameHandlerImpl::class.java.getDeclaredField("state").apply { isAccessible = true }
        val state = stateField.get(gameHandler) as GameState
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>
        val exception = assertThrows<GameException> {
            gameHandler.handle(
                PlayerActionRequest(
                    state.defendPlayer,
                    playersCards.get(state.defendPlayer)!![0],
                    action = PlayerAction.DROP_CARD
                )
            )
        }
        assertEquals("Not your turn yet!", exception.message)
    }

    @Test
    fun `should handle attack when it's player's turn to attack`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)
        gameHandler.startGame()

        val stateField = GameHandlerImpl::class.java.getDeclaredField("state").apply { isAccessible = true }
        val state = stateField.get(gameHandler) as GameState
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>
        val droppedCard = playersCards[state.attackPlayer]!!.get(0)

        gameHandler.handle(
            PlayerActionRequest(
                state.attackPlayer,
                playersCards.get(state.attackPlayer)!![0],
                action = PlayerAction.DROP_CARD
            )
        )
        assertFalse(playersCards[state.attackPlayer]!!.contains(droppedCard))
    }

    @Test
    fun `should handle attack when it's player's turn to attack BEAT`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)
        gameHandler.startGame()

        val stateField = GameHandlerImpl::class.java.getDeclaredField("state").apply { isAccessible = true }
        val state = stateField.get(gameHandler) as GameState
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>
        val droppedCard = playersCards[state.attackPlayer]!!.get(0)

        gameHandler.handle(
            PlayerActionRequest(
                state.attackPlayer,
                playersCards.get(state.attackPlayer)!![0],
                action = PlayerAction.BEAT
            )
        )
        assertTrue(playersCards[state.attackPlayer]!!.contains(droppedCard))
    }

    @Test
    fun `should throw GameException when invalid turn`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)
        gameHandler.startGame()

        val stateField = GameHandlerImpl::class.java.getDeclaredField("state").apply { isAccessible = true }
        val state = stateField.get(gameHandler) as GameState
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>
        val exception = assertThrows<GameException> {
            gameHandler.handle(
                PlayerActionRequest(
                    state.attackPlayer,
                    playersCards.get(state.attackPlayer)!![0],
                    action = PlayerAction.TAKE
                )
            )
        }
        assertEquals("Invalid move when attacking", exception.message)
    }


    @Test
    fun `should handle defense when it's player's turn to defend`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)
        gameHandler.startGame()

        val stateField = GameHandlerImpl::class.java.getDeclaredField("state").apply { isAccessible = true }
        val state = stateField.get(gameHandler) as GameState
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>
        val minCard = findMinCardBySuit(playersCards.get(state.attackPlayer)!!)
        gameHandler.handle(
            PlayerActionRequest(
                state.attackPlayer,
                minCard,
                action = PlayerAction.DROP_CARD
            )
        )

        try {
            gameHandler.handle(
                PlayerActionRequest(
                    state.defendPlayer,
                    findMaxCardBySuit(playersCards.get(state.defendPlayer)!!, minCard!!.suit),
                    action = PlayerAction.DROP_CARD
                )
            )
        }catch (e: GameException){
            gameHandler.handle(
                PlayerActionRequest(
                    state.defendPlayer,
                    null,
                    action = PlayerAction.TAKE
                )
            )
        }
    }

    @Test
    fun `should handle defense when it's player's turn to TAKE`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)
        gameHandler.startGame()

        val stateField = GameHandlerImpl::class.java.getDeclaredField("state").apply { isAccessible = true }
        val state = stateField.get(gameHandler) as GameState
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>
        val minCard = findMinCardBySuit(playersCards.get(state.attackPlayer)!!)
        gameHandler.handle(
            PlayerActionRequest(
                state.attackPlayer,
                minCard,
                action = PlayerAction.DROP_CARD
            )
        )

        gameHandler.handle(
            PlayerActionRequest(
                state.defendPlayer,
                findMaxCardBySuit(playersCards.get(state.defendPlayer)!!, minCard!!.suit),
                action = PlayerAction.TAKE
            )
        )
    }

    @Test
    fun `should handle defense when it's player's turn to BEAT`() {
        val player1 = accountDto
        val player2 = accountDto2
        gameHandler.addPlayer(player1)
        gameHandler.addPlayer(player2)
        gameHandler.startGame()

        val stateField = GameHandlerImpl::class.java.getDeclaredField("state").apply { isAccessible = true }
        val state = stateField.get(gameHandler) as GameState
        val playersCardsField =
            GameHandlerImpl::class.java.getDeclaredField("playersCards").apply { isAccessible = true }
        val playersCards = playersCardsField.get(gameHandler) as MutableMap<Long, MutableList<Card>>
        val minCard = findMinCardBySuit(playersCards.get(state.attackPlayer)!!)

        gameHandler.handle(
            PlayerActionRequest(
                state.attackPlayer,
                minCard,
                action = PlayerAction.DROP_CARD
            )
        )

        val exception = assertThrows<GameException> {
            gameHandler.handle(
                PlayerActionRequest(
                    state.defendPlayer,
                    findMaxCardBySuit(playersCards.get(state.defendPlayer)!!, minCard!!.suit),
                    action = PlayerAction.BEAT
                )
            )
        }
        assertEquals("Invalid move when defending", exception.message)
    }

    fun findMinCardBySuit(cards: MutableList<Card>): Card? {
        var minCard: Card? = null

        for (card in cards) {

            if (minCard == null || card.strength < minCard.strength) {
                minCard = card
            }

        }

        return minCard
    }

    fun findMaxCardBySuit(cards: MutableList<Card>, suit: Suit): Card? {
        var maxCard: Card? = null

        for (card in cards) {
            if (card.suit == suit) {
                if (maxCard == null || card.strength > maxCard.strength) {
                    maxCard = card
                }
            }
        }

        return maxCard
    }
}