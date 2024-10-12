package com.example.roomservice

import com.example.common.exceptions.AccountNotFoundException
import com.example.common.exceptions.RoomNotFoundException
import com.example.common.exceptions.RoomOverflowException
import com.example.gamehandlerservice.model.dto.AccountAction
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.repository.RoomEntity
import com.example.roomservice.repository.RoomRepository
import com.example.roomservice.service.RoomAccountManagerImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.Optional

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

        assertThrows(RoomNotFoundException::class.java) { roomAccountManager.addAccount(999L, accountId) }
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

        assertThrows(AccountNotFoundException::class.java) { roomAccountManager.addAccount(roomId, 999L) }
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

        assertThrows(RoomOverflowException::class.java) { roomAccountManager.addAccount(roomId, 2L) }
    }

    @Test
    fun `addAccount should add account successfully when room has space`() {
        val roomId = 1L
        val accountId = 2L

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(user))

        roomAccountManager.addAccount(roomId, accountId)

        assertTrue(room.players.contains(user))
    }

    @Test
    fun `removeAccount should return roomNotFound if room does not exist`() {
        val accountId = 1L
        `when`(roomRepository.findById(anyLong())).thenReturn(Optional.empty())

        assertThrows(RoomNotFoundException::class.java) {
            roomAccountManager.removeAccount(
                999L,
                accountId,
                AccountAction.LEAVE
            )
        }
    }

    @Test
    fun `removeAccount should return playerNotFound if account does not exist`() {
        val roomId = 1L
        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(anyLong())).thenReturn(Optional.empty())

        assertThrows(AccountNotFoundException::class.java) {
            roomAccountManager.removeAccount(
                roomId,
                999L,
                AccountAction.LEAVE
            )
        }
    }


    @Test
    fun `should remove account from room and save changes`() {
        val roomId = 1L
        val accountId = 2L
        val reason = AccountAction.BAN
        val room = room.apply { players.add(user) }

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(user))

        roomAccountManager.removeAccount(roomId, accountId, reason)

        assertFalse(room.players.contains(user))
        assertTrue(room.bannedPlayers.contains(user))
    }

    @Test
    fun `should throw AccountNotFoundException when account is not in room players`() {
        val roomId = 1L
        val accountId = 2L
        val reason = AccountAction.BAN
        val room = room.apply { players.add(user) }
        val account = AccountEntity(
            name = "User3",
            fines = 0,
            id = 3L
        )

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(account))

        val exception = assertThrows<AccountNotFoundException> {
            roomAccountManager.removeAccount(roomId, accountId, reason)
        }
        assertEquals("Account with id $accountId not found", exception.message)
    }

}