package com.example.gameserviceintegration

import com.example.common.StompIntegrationTestBase
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.dto.MoveCardResponse
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.repository.RoomRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.stomp.StompSession
import kotlin.properties.Delegates

class DistributionStrategyTest : StompIntegrationTestBase() {
    private var userSessions: MutableMap<Long, StompSession> = mutableMapOf()
    private var hostId by Delegates.notNull<Long>()
    private var roomId by Delegates.notNull<Long>()

    private val gameId: Long
        get() = roomRepository.findById(roomId).get().currentGameId
    private val game: GameHandler
        get() = gameHandlerRegistry.getGame(gameId) ?: throw IllegalArgumentException("No game found")

    @Autowired
    private lateinit var gameHandlerRegistry: GameHandlerRegistry

    @Autowired
    private lateinit var roomRepository: RoomRepository

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun initData() {
        val host = accountService.createAccountForUser("name1").id
        val roomDto = roomManager.createRoom("room", host, 3)
        roomId = roomDto.id
        hostId = host
        roomAccountManager.addAccount(roomId, hostId)
        var session = getClientStompSession(roomDto.id, host)
        userSessions[hostId] = session

        for (i in 2..3) {
            val user = accountService.createAccountForUser("name$i").id
            session = getClientStompSession(roomDto.id, user)
            userSessions[user] = session
            roomAccountManager.addAccount(roomId, user)
        }

        game.stateMachine.stage = Stage.DISTRIBUTION
        game.gameData.playersTurnQueue = CyclicQueue(userSessions.keys.map { accountRepository.findById(it).get() })
    }

    @AfterEach
    fun clear() {
        userSessions.clear()
    }

    @Test
    fun checkDropEnemySuccess() {
        val turningPlayerId = game.turningPlayer()?.id ?: throw IllegalArgumentException("No data")
        val otherPlayers = userSessions.keys.filter { it != turningPlayerId }
        game.gameData.userCards[VirtualPlayers.TABLE.id] = linkedSetOf(
            Card(Suit.SPADES, 7, false),
            Card(Suit.SPADES, 10, false)
        )
        game.gameData.userCards[turningPlayerId] = linkedSetOf(
            Card(Suit.SPADES, 8, false),
            Card(Suit.DIAMONDS, 5, false)
        )
        game.gameData.userCards[otherPlayers[0]] = linkedSetOf(
            Card(Suit.SPADES, 9, false),
        )
        game.gameData.userCards[otherPlayers[1]] = linkedSetOf(
            Card(Suit.SPADES, 4, false),
        )
        val lastCard = game.gameData.userCards[VirtualPlayers.TABLE.id]?.last
            ?: throw IllegalArgumentException("No card on table")

        userSessions[turningPlayerId]?.send(
            "/app/move-card", MoveCardRequest(
                VirtualPlayers.TABLE.id,
                otherPlayers[0],
                lastCard
            )
        )
        assertEquals(
            getMessage(turningPlayerId), MoveCardResponse(
                VirtualPlayers.TABLE.id,
                otherPlayers[0],
                lastCard
            )
        )
    }

    @Test
    fun checkDropSelfSuccess() {
        val turningPlayerId = game.turningPlayer()?.id ?: throw IllegalArgumentException("No data")
        val otherPlayers = userSessions.keys.filter { it != turningPlayerId }
        game.gameData.userCards[VirtualPlayers.TABLE.id] = linkedSetOf(
            Card(Suit.SPADES, 7, false),
            Card(Suit.SPADES, 10, false)
        )
        game.gameData.userCards[turningPlayerId] = linkedSetOf(
            Card(Suit.SPADES, 8, false),
            Card(Suit.DIAMONDS, 5, false)
        )
        game.gameData.userCards[otherPlayers[0]] = linkedSetOf(
            Card(Suit.SPADES, 2, false),
        )
        game.gameData.userCards[otherPlayers[1]] = linkedSetOf(
            Card(Suit.SPADES, 2, false),
        )
        val lastCard = game.gameData.userCards[VirtualPlayers.TABLE.id]?.last
            ?: throw IllegalArgumentException("No card on table")

        userSessions[turningPlayerId]?.send(
            "/app/move-card", MoveCardRequest(
                VirtualPlayers.TABLE.id,
                turningPlayerId,
                lastCard
            )
        )
        assertEquals(
            getMessage(turningPlayerId), MoveCardResponse(
                VirtualPlayers.TABLE.id,
                turningPlayerId,
                lastCard
            )
        )
    }

    @Test
    fun checkDropLastCardFromTable() {
        val turningPlayerId = game.turningPlayer()?.id ?: throw IllegalArgumentException("No data")
        val otherPlayers = userSessions.keys.filter { it != turningPlayerId }
        game.gameData.userCards[VirtualPlayers.TABLE.id] = linkedSetOf(
            Card(Suit.SPADES, 7, false)
        )
        game.gameData.userCards[turningPlayerId] = linkedSetOf(
            Card(Suit.SPADES, 8, false),
            Card(Suit.DIAMONDS, 5, false)
        )
        game.gameData.userCards[otherPlayers[0]] = linkedSetOf(
            Card(Suit.SPADES, 6, false),
        )
        game.gameData.userCards[otherPlayers[1]] = linkedSetOf(
            Card(Suit.SPADES, 2, false),
        )
        val lastCard = game.gameData.userCards[VirtualPlayers.TABLE.id]?.last
            ?: throw IllegalArgumentException("No card on table")

        userSessions[turningPlayerId]?.send(
            "/app/move-card", MoveCardRequest(
                VirtualPlayers.TABLE.id,
                turningPlayerId,
                lastCard
            )
        )
        assertEquals(
            getMessage(turningPlayerId), MoveCardResponse(
                VirtualPlayers.TABLE.id,
                turningPlayerId,
                lastCard
            )
        )
        assertEquals(game.stateMachine.stage, Stage.FINES)
    }
}