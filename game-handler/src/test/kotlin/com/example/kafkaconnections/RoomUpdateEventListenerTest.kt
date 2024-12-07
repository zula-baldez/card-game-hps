package com.example.kafkaconnections

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.common.kafkaconnections.RoomUpdateEvent
import com.example.gamehandlerservice.kafkaconnections.RoomUpdateEventListener
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RoomUpdateEventListenerTest {

    private lateinit var roomUpdateEventListener: RoomUpdateEventListener
    private val registry: GameHandlerRegistry = mock()

    @BeforeEach
    fun setUp() {
        roomUpdateEventListener = RoomUpdateEventListener(registry)
    }

    @Test
    fun `should create game on ROOM_CREATED event`() {
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val event = RoomUpdateEvent(
            roomId = 1L,
            eventType = RoomUpdateEvent.Companion.RoomUpdateEventType.ROOM_CREATED,
            roomDto = roomDto
        )

        roomUpdateEventListener.listen(event)

        verify(registry).createGame(roomDto)
    }

    @Test
    fun `should throw exception if roomDto is null on ROOM_CREATED event`() {
        val event = RoomUpdateEvent(
            roomId = 1L,
            eventType = RoomUpdateEvent.Companion.RoomUpdateEventType.ROOM_CREATED,
            roomDto = null
        )

        assertThrows<IllegalArgumentException> {
            roomUpdateEventListener.listen(event)
        }

    }

    @Test
    fun `should delete game on ROOM_DELETED event`() {
        val event = RoomUpdateEvent(roomId = 1, eventType = RoomUpdateEvent.Companion.RoomUpdateEventType.ROOM_DELETED)

        roomUpdateEventListener.listen(event)

        verify(registry).deleteGame(1)
    }

    @Test
    fun `should throw exception if room not found on PLAYER_JOIN event`() {
        val event = RoomUpdateEvent(
            roomId = 1L,
            eventType = RoomUpdateEvent.Companion.RoomUpdateEventType.PLAYER_JOIN,
            newPlayer = AccountDto(id = 2L, name = "Player1", fines = 0, "avatar", roomId = 1L)
        )

        whenever(registry.getGame(1L)).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            roomUpdateEventListener.listen(event)
        }

    }

    @Test
    fun `should throw exception if newPlayer is null on PLAYER_JOIN event`() {
        val event = RoomUpdateEvent(
            roomId = 1L,
            eventType = RoomUpdateEvent.Companion.RoomUpdateEventType.PLAYER_JOIN,
            newPlayer = null
        )

        whenever(registry.getGame(1L)).thenReturn(mock())

        assertThrows<IllegalArgumentException> {
            roomUpdateEventListener.listen(event)
        }

    }

    @Test
    fun `should throw exception if room not found on PLAYER_LEAVE event`() {

        val event = RoomUpdateEvent(
            roomId = 1L,
            eventType = RoomUpdateEvent.Companion.RoomUpdateEventType.PLAYER_LEAVE,
            playerLeave = RoomUpdateEvent.Companion.PlayerLeaveEvent(1L, 2L)
        )
        whenever(registry.getGame(1L)).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            roomUpdateEventListener.listen(event)
        }
    }

    @Test
    fun `should throw exception if playerLeave is null on PLAYER_LEAVE event`() {
        val event = RoomUpdateEvent(
            roomId = 1L,
            eventType = RoomUpdateEvent.Companion.RoomUpdateEventType.PLAYER_LEAVE,
            playerLeave = null
        )

        whenever(registry.getGame(1L)).thenReturn(mock())

        assertThrows<IllegalArgumentException> {
            roomUpdateEventListener.listen(event)
        }

    }

    @Test
    fun `should remove player and update host successfully`() {
        val gameMock = Mockito.mock(GameHandler::class.java)

        val data = RoomUpdateEvent(
            roomId = 1L,
            eventType = RoomUpdateEvent.Companion.RoomUpdateEventType.PLAYER_LEAVE,
            playerLeave = RoomUpdateEvent.Companion.PlayerLeaveEvent(1L, 2L)
        )

        whenever(registry.getGame(data.roomId)).thenReturn(gameMock)

        roomUpdateEventListener.listen(data)

        verify(gameMock).removePlayer(1L)
        verify(gameMock).updateHostId(2L)
    }


}