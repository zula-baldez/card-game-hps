package com.example.roomservice

import com.example.common.exceptions.*
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
import org.mockito.kotlin.verify
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.Optional

class RoomAccountManagerTest {

    private lateinit var roomAccountManager: RoomAccountManagerImpl
    private lateinit var roomRepository: RoomRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var messagingTemplate: SimpMessagingTemplate
    private val hostId = 1L
    private val playerId = 2L
    private var host = AccountEntity(
        name = "User1",
        fines = 0,
        id = hostId
    )
    private var player = AccountEntity(
        name = "User2",
        fines = 0,
        id = playerId
    )
    private var room = RoomEntity(
        id = 0,
        name = "Комната для игры",
        hostId = hostId,
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

        assertThrows(RoomNotFoundException::class.java) { roomAccountManager.addAccount(999L, accountId, accountId) }
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

        assertThrows(AccountNotFoundException::class.java) { roomAccountManager.addAccount(roomId, 999L, 999L) }
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
        `when`(accountRepository.findById(hostId)).thenReturn(Optional.of(host))

        assertThrows(RoomOverflowException::class.java) { roomAccountManager.addAccount(roomId, hostId, hostId) }
    }

    @Test
    fun `addAccount should add account successfully when room has space`() {
        val roomId = 1L
        val accountId = 2L

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(host))

        roomAccountManager.addAccount(roomId, accountId, accountId)

        assertTrue(room.players.contains(host))
    }

    @Test
    fun `removeAccount should return roomNotFound if room does not exist`() {
        val accountId = 1L
        `when`(roomRepository.findById(anyLong())).thenReturn(Optional.empty())

        assertThrows(RoomNotFoundException::class.java) {
            roomAccountManager.removeAccount(
                999L,
                accountId,
                AccountAction.LEAVE,
                accountId
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
                AccountAction.LEAVE,
                hostId
            )
        }
    }


    @Test
    fun `should remove account from room and save changes`() {
        val roomId = 1L
        val accountId = 1L
        val reason = AccountAction.BAN
        val room = room.apply { players.add(host) }

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(host))

        roomAccountManager.removeAccount(roomId, accountId, reason, accountId)

        assertFalse(room.players.contains(host))
        assertTrue(room.bannedPlayers.contains(host))
    }

    @Test
    fun `should throw AccountNotFoundException when account is not in room players`() {
        val roomId = 1L
        val accountId = 1L
        val reason = AccountAction.BAN
        val room = room.apply { players.add(host) }
        val account = AccountEntity(
            name = "User3",
            fines = 0,
            id = 3L
        )

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(account))

        val exception = assertThrows<AccountNotFoundException> {
            roomAccountManager.removeAccount(roomId, accountId, reason, accountId)
        }
        assertEquals("Account with id $accountId not found", exception.message)
    }

    @Test
    fun `should throw when account is not host when remove player`() {
        val roomId = 1L
        val hostId = 1L
        val userId = 3L

        val reason = AccountAction.BAN
        val room = room.apply { players.add(host) }
        val hostAccount = AccountEntity(
            name = "User1",
            fines = 0,
            id = 1L
        )

        val userAccount = AccountEntity(
            name = "User3",
            fines = 0,
            id = 3L
        )

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(hostId)).thenReturn(Optional.of(hostAccount))
        `when`(accountRepository.findById(userId)).thenReturn(Optional.of(userAccount))

        val exception = assertThrows<HostOnlyException> {
            roomAccountManager.removeAccount(roomId, hostId, reason, userId)
        }
        assertEquals("This operation is host only", exception.message)
    }

    @Test
    fun `should throw ForbiddenOperationException when requesterId does not match accountId`() {
        val roomId = 1L
        val accountId = 2L
        val requesterId = 3L

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(room))
        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(host))

       assertThrows<ForbiddenOperationException> {
            roomAccountManager.addAccount(roomId, accountId, requesterId)
        }
    }

    @Test
    fun `should transfer host after host leaves`() {
        val roomId = 1L

        val initialRoom = RoomEntity(
            id = roomId,
            name = "Комната для игры",
            hostId = hostId,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(
                host,
                player
            )
        )

        `when`(roomRepository.findById(roomId)).thenReturn(Optional.of(initialRoom))
        `when`(accountRepository.findById(hostId)).thenReturn(Optional.of(host))
        `when`(accountRepository.findById(playerId)).thenReturn(Optional.of(player))

        roomAccountManager.removeAccount(roomId, hostId, AccountAction.LEAVE, hostId)

        assertEquals(initialRoom.hostId, playerId)
        assertFalse(initialRoom.players.contains(host))
    }
}