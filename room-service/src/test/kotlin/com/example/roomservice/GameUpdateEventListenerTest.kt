package com.example.roomservice

import com.example.common.dto.roomservice.AccountAction
import com.example.common.kafkaconnections.GameUpdateEvent
import com.example.roomservice.kafkaconnections.GameUpdateEventListener
import com.example.roomservice.service.RoomAccountManager
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import reactor.core.publisher.Mono

class GameUpdateEventListenerTest {

    private lateinit var roomAccountManager: RoomAccountManager
    private lateinit var gameUpdateEventListener: GameUpdateEventListener

    @BeforeEach
    fun setUp() {
        roomAccountManager = mock(RoomAccountManager::class.java)
        gameUpdateEventListener = GameUpdateEventListener(roomAccountManager)
    }

    @Test
    fun `listen should remove account on PLAYER_DISCONNECT event`() {
        val accountId = 123L
        val roomId = 456L
        val event = GameUpdateEvent(
            eventType = GameUpdateEvent.Companion.GameUpdateEventType.PLAYER_DISCONNECT,
            roomId = roomId,
            playerDisconnect = GameUpdateEvent.Companion.PlayerDisconnectGameUpdateEvent(accountId)
        )

        `when`(roomAccountManager.removeAccount(roomId, accountId, AccountAction.KICK))
            .thenReturn(Mono.empty())

        gameUpdateEventListener.listen(event)

        verify(roomAccountManager).removeAccount(roomId, accountId, AccountAction.KICK)
    }

    @Test
    fun `listen should throw IllegalArgumentException for broken message`() {
        val event = GameUpdateEvent(
            eventType = GameUpdateEvent.Companion.GameUpdateEventType.PLAYER_DISCONNECT,
            roomId = 456L,
            playerDisconnect = null
        )

        assertThrows(IllegalArgumentException::class.java) {
            gameUpdateEventListener.listen(event)
        }
    }

    @Test
    fun `getUsernamePasswordAuthenticationToken should return valid token`() {
        val uid = 123L

        val token: UsernamePasswordAuthenticationToken =
            gameUpdateEventListener.getUsernamePasswordAuthenticationToken(uid)

        assert(token.principal == uid)
        assert(token.credentials == null)
        assert(token.authorities.size == 1)
        assert(token.authorities.first().authority == "USER")
    }

    @Test
    fun `listen should handle exceptions gracefully`() {
        val accountId = 123L
        val roomId = 456L
        val event = GameUpdateEvent(
            eventType = GameUpdateEvent.Companion.GameUpdateEventType.PLAYER_DISCONNECT,
            roomId = roomId,
            playerDisconnect = GameUpdateEvent.Companion.PlayerDisconnectGameUpdateEvent(accountId)
        )

        `when`(roomAccountManager.removeAccount(roomId, accountId, AccountAction.KICK))
            .thenThrow(RuntimeException("Test exception"))

        gameUpdateEventListener.listen(event)

        verify(roomAccountManager).removeAccount(roomId, accountId, AccountAction.KICK)
    }
}