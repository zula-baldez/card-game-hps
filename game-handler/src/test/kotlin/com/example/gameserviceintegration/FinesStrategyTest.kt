package com.example.gameserviceintegration

import com.example.common.StompIntegrationTestBase
import com.example.gamehandlerservice.model.dto.MoveCardRequest
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.stomp.StompSession
import kotlin.properties.Delegates
import org.springframework.data.repository.findByIdOrNull
import java.util.LinkedHashSet

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

    private val commonCard = Card(Suit.CLUBS, 1, false)

    @BeforeEach
    fun initData() {
        val hostId = accountService.createAccountForUser("name1").id
        val roomDto = roomManager.createRoom("room", hostId, 3)
        roomId = roomDto.id
        this.hostId = hostId
        game.stateMachine.stage = Stage.FINES
        roomAccountManager.addAccount(roomId, this.hostId, this.hostId)
        var session = getClientStompSession(roomDto.id, hostId)
        userSessions[this.hostId] = session
        game.gameData.userCards[this.hostId] = linkedSetOf(commonCard)
        game.gameData.userCards[VirtualPlayers.TABLE.id] = LinkedHashSet()

        for (i in 2L..3L) {
            val userId = accountService.createAccountForUser("name$i").id
            session = getClientStompSession(roomDto.id, userId)
            userSessions[userId] = session
            roomAccountManager.addAccount(roomId, userId, userId)
            game.gameData.userCards[userId] = linkedSetOf(commonCard)
        }

        game.gameData.playersTurnQueue = CyclicQueue(userSessions.keys.map { accountRepository.findById(it).get() })
    }

    @AfterEach
    fun clear() {
        roomManager.deleteRoom(roomId)
        userSessions.keys.forEach { accountRepository.deleteById(it) }
        userSessions.clear()
    }

    private fun getTurningPlayer(): Long? {
        return game.turningPlayer()?.id
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
        userSessions[notTurningPlayer]?.send(
            "/app/move-card",
            MoveCardRequest(notTurningPlayer, turningPlayer, commonCard)
        )
        val response = getMessage(notTurningPlayer)
        assertNull(response)
    }

    @Test
    fun dropOnEnemyWithFines() {
        val notTurningPlayer = getNotTurningPlayer()
        val turningPlayer = getTurningPlayer()!!
        var finedAccount = accountRepository.findByIdOrNull(notTurningPlayer)!!
        finedAccount.fines = 1
        accountRepository.save(finedAccount)
        userSessions[turningPlayer]?.send(
            "/app/move-card",
            MoveCardRequest(turningPlayer, notTurningPlayer, commonCard)
        )
        val response = getMessage(turningPlayer)
        assertEquals(commonCard, response?.card)
        assertEquals(notTurningPlayer, response?.idTo)
        assertEquals(turningPlayer, response?.idFrom)
        finedAccount =
            accountRepository.findByIdOrNull(notTurningPlayer) ?: throw IllegalArgumentException("Player not found???")
        assertEquals(0, finedAccount.fines)
    }

    @Test
    fun dropOnEnemyWithNoFines() {
        val notTurningPlayer = getNotTurningPlayer()
        val turningPlayer = getTurningPlayer()!!
        val finedAccount = accountRepository.findByIdOrNull(notTurningPlayer)!!
        finedAccount.fines = 0
        accountRepository.save(finedAccount)
        userSessions[turningPlayer]?.send(
            "/app/move-card",
            MoveCardRequest(turningPlayer, notTurningPlayer, commonCard)
        )
        val response = getMessage(turningPlayer)
        assertNull(response)
    }

    @Test
    fun fineOnDropTable() {
        val turningPlayer = getTurningPlayer()!!
        var finedAccount = accountRepository.findByIdOrNull(turningPlayer)!!
        finedAccount.fines = 0
        accountRepository.save(finedAccount)
        userSessions[turningPlayer]?.send(
            "/app/move-card",
            MoveCardRequest(turningPlayer, VirtualPlayers.TABLE.id, commonCard)
        )
        val response = getMessage(turningPlayer)
        assertNull(response)

        finedAccount = accountRepository.findByIdOrNull(turningPlayer)!!
        assertEquals(1, finedAccount.fines)
    }
}