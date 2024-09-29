package com.example.roomservice

import com.example.gamehandlerservice.model.dto.AccountAction
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.repository.RoomEntity
import com.example.roomservice.repository.RoomRepository
import com.example.roomservice.service.RoomAccountManagerImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.*

class RoomAccountManagerTest {

    private lateinit var roomAccountManager: RoomAccountManagerImpl
    private lateinit var roomRepository: RoomRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var messagingTemplate: SimpMessagingTemplate
    private val accountId = 2L
    private var user = AccountEntity(
        name = "User1",
        fines = 0,
        id = accountId
    )
    private var room = RoomEntity(
        id = 0,
        name = "Комната для игры",
        hostId = 1L,
        capacity = 4,
        currentGameId = 0L,
        players = mutableListOf()
    )

    @BeforeEach
    fun setUp() {
        roomRepository = mock(RoomRepository::class.java)
        accountRepository = mock(AccountRepository::class.java)
        messagingTemplate = mock(SimpMessagingTemplate::class.java)

        roomAccountManager = RoomAccountManagerImpl(roomRepository, accountRepository, messagingTemplate)
    }

    @Test
    fun `addAccount should return roomNotFound if room does not exist`() {
        val accountId = 1L
        `when`(roomRepository.findById(anyLong())).thenReturn(Optional.empty())

        val result = roomAccountManager.addAccount(999L, accountId)

        assertFalse(result.success)
        assertEquals("no such room", result.reason)
    }

    @Test
    fun `addAccount should return playerNotFound if account does not exist`() {
        val roomId = 1L
        `when`(roomRepository.findById(roomId)).thenReturn(
            Optional.of(
                room
            )
        )
        `when`(accountRepository.findById(anyLong())).thenReturn(Optional.empty())

        val result = roomAccountManager.addAccount(roomId, 999L)

        assertFalse(result.success)
        assertEquals("no such player", result.reason)
    }

    @Test
    fun `addAccount should return roomOverflow if room is full`() {
        val roomId = 1L
        val room = RoomEntity(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 0,
            currentGameId = 0L,
            players = mutableListOf()
        )

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(user))

        val result = roomAccountManager.addAccount(roomId, 2L)
        assertFalse(result.success)
        assertEquals("Room is full!", result.reason)
    }

    @Test
    fun `addAccount should add account successfully when room has space`() {
        val roomId = 1L
        val accountId = 2L

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(user))

        val result = roomAccountManager.addAccount(roomId, accountId)

        assertTrue(result.success)
        assertNull(result.reason)
        assertTrue(room.players.contains(user))
    }

    @Test
    fun `removeAccount should return roomNotFound if room does not exist`() {
        val accountId = 1L
        `when`(roomRepository.findById(anyLong())).thenReturn(Optional.empty())

        val result = roomAccountManager.removeAccount(999L, accountId, AccountAction.LEAVE)

        assertFalse(result.success)
        assertEquals("no such room", result.reason)
    }

    @Test
    fun `removeAccount should return playerNotFound if account does not exist`() {
        val roomId = 1L
        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(anyLong())).thenReturn(Optional.empty())

        val result = roomAccountManager.removeAccount(roomId, 999L, AccountAction.LEAVE)

        assertFalse(result.success)
        assertEquals("no such player", result.reason)
    }
    @Test
    fun `should return roomNotFound when room does not exist`() {
        val roomId = 1L
        val accountId = 1L
        val reason = AccountAction.LEAVE

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.empty())

        val result = roomAccountManager.removeAccount(roomId, accountId, reason)

        assertEquals("no such room", result.reason)
    }

}