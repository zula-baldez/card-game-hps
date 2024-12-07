package com.example.roomservice

import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.dto.roomservice.AccountAction
import com.example.common.exceptions.AccountNotFoundException
import com.example.common.exceptions.ForbiddenOperationException
import com.example.common.exceptions.RoomOverflowException
import com.example.roomservice.repository.*
import com.example.roomservice.service.RoomAccountManagerImpl
import com.example.roomservice.service.RoomUpdateEventSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class RoomAccountManagerTest {
    private val roomRepository: RoomRepository = mock(RoomRepository::class.java)
    private val accountInRoomRepository: AccountInRoomRepository = mock(AccountInRoomRepository::class.java)
    private val bannedAccountInRoomRepository: BannedAccountInRoomRepository =
        mock(BannedAccountInRoomRepository::class.java)
    private val personalAccountClient: ReactivePersonalAccountClient = mock(ReactivePersonalAccountClient::class.java)
    private val sender: RoomUpdateEventSender = mock(RoomUpdateEventSender::class.java)

    private val roomAccountManager = RoomAccountManagerImpl(
        roomRepository,
        accountInRoomRepository,
        bannedAccountInRoomRepository,
        personalAccountClient,
        sender
    )


    @Test
    fun `addAccount should add account successfully`() {
        val roomId = 100L
        val accountId = 1L
        val roomEntity = RoomEntity(roomId, "name", accountId, 10, 1)

        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.empty())
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        `when`(accountInRoomRepository.save(any())).thenReturn(Mono.just(AccountInRoomEntity(accountId, roomId)))
        `when`(personalAccountClient.updateAccountRoom(accountId, UpdateAccountRoomRequest(roomId)))
            .thenReturn(Mono.empty())

        StepVerifier.create(roomAccountManager.addAccount(roomId, accountId))
            .verifyComplete()

        verify(accountInRoomRepository).save(any())
    }

    @Test
    fun `addAccount should throw error on banned account`() {
        val roomId = 100L
        val hostId = 1L
        val accountId = 2L
        val roomEntity = RoomEntity(roomId, "name", hostId, 10, 1)
        val hostInRoomEntity = AccountInRoomEntity(hostId, roomId)
        val accountInRoomEntity = AccountInRoomEntity(accountId, roomId)
        val bannedAccountInRoomEntity = BannedAccountInRoomEntity(0, accountId, roomId)

        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(hostInRoomEntity))
        `when`(accountInRoomRepository.findById(hostId)).thenReturn(Mono.just(hostInRoomEntity))
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.just(accountInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(bannedAccountInRoomEntity))

        StepVerifier.create(roomAccountManager.addAccount(roomId, accountId))
            .verifyError(ForbiddenOperationException::class.java)
    }

    @Test
    fun `addAccount should throw error capacity`() {
        val roomId = 100L
        val hostId = 1L
        val accountId = 2L
        val roomEntity = RoomEntity(roomId, "name", hostId, 1, 1)
        val hostInRoomEntity = AccountInRoomEntity(hostId, roomId)
        val accountInRoomEntity = AccountInRoomEntity(accountId, roomId)

        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(hostInRoomEntity))
        `when`(accountInRoomRepository.findById(hostId)).thenReturn(Mono.just(hostInRoomEntity))
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.just(accountInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())

        StepVerifier.create(roomAccountManager.addAccount(roomId, accountId))
            .verifyError(RoomOverflowException::class.java)
    }

    @Test
    fun `addAccount should throw when join twice`() {
        val roomId = 100L
        val hostId = 1L
        val roomEntity = RoomEntity(roomId, "name", hostId, 1, 1)
        val hostInRoomEntity = AccountInRoomEntity(hostId, roomId)

        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(hostInRoomEntity))
        `when`(accountInRoomRepository.findById(hostId)).thenReturn(Mono.just(hostInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())

        StepVerifier.create(roomAccountManager.addAccount(roomId, hostId))
            .verifyError(ForbiddenOperationException::class.java)
    }

    @Test
    fun `addAccount should throw when account in another room`() {
        val roomId = 100L
        val hostId = 1L
        val accountId = 2L
        val roomEntity = RoomEntity(roomId, "name", hostId, 2, 1)
        val hostInRoomEntity = AccountInRoomEntity(hostId, roomId)
        val accountInRoomEntity = AccountInRoomEntity(accountId, 101L)

        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(hostInRoomEntity))
        `when`(accountInRoomRepository.findById(hostId)).thenReturn(Mono.just(hostInRoomEntity))
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.just(accountInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())

        StepVerifier.create(roomAccountManager.addAccount(roomId, accountId))
            .verifyError(ForbiddenOperationException::class.java)
    }

    @Test
    fun `removeAccount should delete room when last player leaves`() {
        val roomId = 100L
        val accountId = 1L
        val roomEntity = RoomEntity(roomId, "name", 1, 10, 1)
        val accountInRoomEntity = AccountInRoomEntity(accountId, roomId)
        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(accountInRoomEntity))
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.just(accountInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        `when`(accountInRoomRepository.delete(accountInRoomEntity)).thenReturn(Mono.empty())
        `when`(roomRepository.delete(roomEntity)).thenReturn(Mono.empty())
        `when`(personalAccountClient.updateAccountRoom(any(), any())).thenReturn(Mono.empty())
        `when`(roomRepository.deleteById(roomId)).thenReturn(Mono.empty())


        StepVerifier.create(roomAccountManager.removeAccount(roomId, accountId, AccountAction.LEAVE))
            .verifyComplete()
    }

    @Test
    fun `removeAccount should fail on unknown account`() {
        val roomId = 100L
        val accountId = 1L
        val roomEntity = RoomEntity(roomId, "name", 1, 10, 1)
        val accountInRoomEntity = AccountInRoomEntity(accountId, roomId)
        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(accountInRoomEntity))
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.just(accountInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        `when`(accountInRoomRepository.delete(accountInRoomEntity)).thenReturn(Mono.empty())
        `when`(roomRepository.delete(roomEntity)).thenReturn(Mono.empty())
        `when`(
            personalAccountClient.updateAccountRoom(
                accountId,
                UpdateAccountRoomRequest(null)
            )
        ).thenReturn(Mono.empty())

        StepVerifier.create(roomAccountManager.removeAccount(roomId, 2L, AccountAction.LEAVE))
            .verifyError(AccountNotFoundException::class.java)
    }

    @Test
    fun `removeAccount should remove account from room`() {
        val roomId = 100L
        val hostId = 1L
        val accountId = 2L
        val roomEntity = RoomEntity(roomId, "name", hostId, 10, 1)
        val hostInRoomEntity = AccountInRoomEntity(hostId, roomId)
        val accountInRoomEntity = AccountInRoomEntity(accountId, roomId)

        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(
            Flux.just(
                hostInRoomEntity,
                accountInRoomEntity
            )
        )
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.just(accountInRoomEntity))
        `when`(accountInRoomRepository.findById(hostId)).thenReturn(Mono.just(hostInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        `when`(roomRepository.save(any())).thenReturn(Mono.empty())
        `when`(accountInRoomRepository.delete(any())).thenReturn(Mono.empty())
        `when`(personalAccountClient.updateAccountRoom(any(), any())).thenReturn(Mono.empty())

        StepVerifier.create(roomAccountManager.removeAccount(roomId, accountId, AccountAction.LEAVE))
            .verifyComplete()
        verify(accountInRoomRepository).delete(accountInRoomEntity)
    }

    @Test
    fun `removeAccount for host should transfer host`() {
        val roomId = 100L
        val hostId = 1L
        val accountId = 2L
        val roomEntity = RoomEntity(roomId, "name", hostId, 10, 1)
        val hostInRoomEntity = AccountInRoomEntity(hostId, roomId)
        val accountInRoomEntity = AccountInRoomEntity(accountId, roomId)

        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(
            Flux.just(
                hostInRoomEntity,
                accountInRoomEntity
            )
        )
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.just(accountInRoomEntity))
        `when`(accountInRoomRepository.findById(hostId)).thenReturn(Mono.just(hostInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        `when`(roomRepository.save(any())).thenReturn(Mono.empty())
        `when`(accountInRoomRepository.delete(any())).thenReturn(Mono.empty())
        `when`(personalAccountClient.updateAccountRoom(any(), any())).thenReturn(Mono.empty())

        StepVerifier.create(roomAccountManager.removeAccount(roomId, hostId, AccountAction.LEAVE))
            .verifyComplete()
        verify(accountInRoomRepository).delete(hostInRoomEntity)
        val newRoomCaptor = argumentCaptor<RoomEntity>()
        verify(roomRepository).save(newRoomCaptor.capture())
        assertEquals(accountId, newRoomCaptor.firstValue.hostId)
    }

    @Test
    fun `removeAccount with BAN should add account to banned accounts`() {
        val roomId = 100L
        val hostId = 1L
        val accountId = 2L
        val roomEntity = RoomEntity(roomId, "name", hostId, 10, 1)
        val hostInRoomEntity = AccountInRoomEntity(hostId, roomId)
        val accountInRoomEntity = AccountInRoomEntity(accountId, roomId)

        `when`(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(
            Flux.just(
                hostInRoomEntity,
                accountInRoomEntity
            )
        )
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(Mono.just(accountInRoomEntity))
        `when`(accountInRoomRepository.findById(hostId)).thenReturn(Mono.just(hostInRoomEntity))
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        `when`(bannedAccountInRoomRepository.save(any())).thenReturn(Mono.empty())
        `when`(roomRepository.save(any())).thenReturn(Mono.empty())
        `when`(accountInRoomRepository.delete(any())).thenReturn(Mono.empty())
        `when`(personalAccountClient.updateAccountRoom(any(), any())).thenReturn(Mono.empty())

        StepVerifier.create(roomAccountManager.removeAccount(roomId, accountId, AccountAction.BAN))
            .verifyComplete()
        verify(accountInRoomRepository).delete(accountInRoomEntity)
        val bannedAccountInRoomCaptor = argumentCaptor<BannedAccountInRoomEntity>()
        verify(bannedAccountInRoomRepository).save(bannedAccountInRoomCaptor.capture())
        assertEquals(accountId, bannedAccountInRoomCaptor.firstValue.accountId)
        assertEquals(roomId, bannedAccountInRoomCaptor.firstValue.roomId)
    }

    @Test
    fun `getAccountRoom should return roomId for given accountId`() {
        val accountId = 1L
        val roomId = 100L
        `when`(accountInRoomRepository.findById(accountId)).thenReturn(
            Mono.just(
                AccountInRoomEntity(
                    accountId,
                    roomId
                )
            )
        )

        val result = roomAccountManager.getAccountRoom(accountId).block()

        assertEquals(roomId, result)
        verify(accountInRoomRepository).findById(accountId)
    }

    @Test
    fun `getAccountsInRoom should return list of accountIds for given roomId`() {
        val roomId = 100L
        val accountIds = listOf(1L, 2L, 3L)
        `when`(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(
            Flux.just(
                AccountInRoomEntity(1L, roomId),
                AccountInRoomEntity(2L, roomId),
                AccountInRoomEntity(3L, roomId)
            )
        )

        val result = roomAccountManager.getAccountsInRoom(roomId).collectList().block()

        assertEquals(accountIds, result)
        verify(accountInRoomRepository).findAllByRoomId(roomId)
    }

    @Test
    fun `getBannedAccountsInRoom should return list of banned accountIds for given roomId`() {
        val roomId = 100L
        val bannedAccountIds = listOf(1L, 2L)
        `when`(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(
            Flux.just(
                BannedAccountInRoomEntity(1L, 1L, roomId),
                BannedAccountInRoomEntity(2L, 2L, roomId)
            )
        )
        val result = roomAccountManager.getBannedAccountsInRoom(roomId).collectList().block()

        assertEquals(bannedAccountIds, result)
        verify(bannedAccountInRoomRepository).findAllByRoomId(roomId)
    }

    @Test
    fun `should initialize AccountInRoomEntity with given parameters`() {
        val accountId = 1L
        val roomId = 101L

        val accountInRoom = AccountInRoomEntity(accountId, roomId)

        assertEquals(accountId, accountInRoom.accountId)
        assertEquals(roomId, accountInRoom.roomId)
        assertEquals(false, accountInRoom.isNewAccount)
    }

    @Test
    fun `should initialize AccountInRoomEntity as new account`() {
        val accountId = 2L
        val roomId = 202L
        val isNewAccount = true

        val accountInRoom = AccountInRoomEntity(accountId, roomId, isNewAccount)

        assertEquals(accountId, accountInRoom.accountId)
        assertEquals(roomId, accountInRoom.roomId)
        assertEquals(isNewAccount, accountInRoom.isNewAccount)
    }

    @Test
    fun `should initialize BannedAccountInRoomEntity with given parameters`() {
        val id = 1L
        val accountId = 2L
        val roomId = 3L

        val bannedAccountInRoom = BannedAccountInRoomEntity(id, accountId, roomId)

        assertEquals(id, bannedAccountInRoom.id)
        assertEquals(accountId, bannedAccountInRoom.accountId)
        assertEquals(roomId, bannedAccountInRoom.roomId)
    }


}