package com.example.gameserviceintegration

import com.example.common.StompIntegrationTestBase
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.repository.RoomRepository
import net.bytebuddy.pool.TypePool.Resolution.Illegal
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.stomp.StompSession
import kotlin.properties.Delegates
import org.springframework.data.repository.findByIdOrNull

class FinesStrategyTest : StompIntegrationTestBase() {
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

    private val commonCard = Card(Suit.Clubs, 1, false)

    @BeforeEach
    fun initData() {
        val host = userService.register("name1", "pass1")
        val roomDto = roomManager.createRoom("room", host.id, 3)
        roomId = roomDto.id
        hostId = host.id
        game.stateMachine.stage = Stage.FINES
        roomAccountManager.addAccount(roomId, hostId)
        var session = getClientStompSession(roomDto.id, host.id, host.token)
        userSessions[hostId] = session

        for (i in 2..3) {
            val user = userService.register("name$i", "pass$i")
            session = getClientStompSession(roomDto.id, user.id, user.token)
            userSessions[user.id] = session
            roomAccountManager.addAccount(roomId, user.id)
        }

        for (i in 1L..3L) {
            game.gameData.userCards[i] = linkedSetOf(commonCard)
        }

        game.gameData.playersTurnQueue = CyclicQueue(userSessions.keys.map { accountRepository.findById(it).get() })
    }

    @AfterEach
    fun clear() {
        userSessions.clear()
    }

    private fun getTurningPlayer(): Long? {
        return game?.turningPlayer()?.id
    }

    private fun getNotTurningPlayer(): Long {
        val turningPlayer = getTurningPlayer()
        return if (turningPlayer == hostId) hostId + 1
        else hostId
    }

    @Test
    fun testCannotDropOnYourself() {
        userSessions[hostId]?.send("/app/move-card", MoveCardRequest(hostId, hostId, commonCard))
        val response = getMessage(hostId)
        assertNull(response)
    }

    @Test
    fun noDropOnEnemyWhenNotYourTurn() {
        val notTurningPlayer = getNotTurningPlayer()
        val turningPlayer = getTurningPlayer()!!
        userSessions[notTurningPlayer]?.send("/app/move-card", MoveCardRequest(notTurningPlayer, turningPlayer, commonCard))
        val response = getMessage(notTurningPlayer)
        assertNull(response)
    }

    @Test
    fun dropOnEnemyWithFines() {
        val notTurningPlayer = getNotTurningPlayer()
        val turningPlayer = getTurningPlayer()!!
        var finedAccount = accountRepository.findByIdOrNull(notTurningPlayer)!!
        finedAccount.fines++
        accountRepository.save(finedAccount)
        userSessions[turningPlayer]?.send("/app/move-card", MoveCardRequest(turningPlayer, notTurningPlayer, commonCard))
        val response = getMessage(turningPlayer)
        assertEquals(commonCard, response?.card)
        assertEquals(notTurningPlayer, response?.idTo)
        assertEquals(turningPlayer, response?.idFrom)
    }
}