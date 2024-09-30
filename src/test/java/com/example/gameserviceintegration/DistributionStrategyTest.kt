package com.example.gameserviceintegration

import com.example.common.StompIntegrationTestBase
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import com.example.roomservice.repository.RoomRepository
import org.junit.jupiter.api.AfterEach
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
    private val game: GameHandler?
        get() = gameHandlerRegistry.getGame(gameId)
    @Autowired
    private lateinit var gameHandlerRegistry: GameHandlerRegistry
    @Autowired
    private lateinit var roomRepository: RoomRepository

    @BeforeEach
    fun initData() {
        val host = userService.register("name1", "pass1")
        val roomDto = roomManager.createRoom("room", host.id, 10)
        game?.stateMachine?.stage = Stage.DISTRIBUTION
        roomId = roomDto.id
        hostId = host.id
        roomAccountManager.addAccount(roomId, hostId)
        var session = getClientStompSession(roomDto.id, host.id, host.token)
        userSessions[hostId] = session

        for (i in 2..10) {
            val user = userService.register("name$i", "pass$i")
            session = getClientStompSession(roomDto.id, user.id, user.token)
            userSessions[user.id] = session
            roomAccountManager.addAccount(roomId, user.id)
        }
        game.
    }

    @AfterEach
    fun clear() {
        userSessions.clear()
    }

    @Test
}