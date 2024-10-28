package com.example.roomservice

import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.dto.roomservice.AccountAction
import com.example.common.exceptions.AccountNotFoundException
import com.example.common.exceptions.ForbiddenOperationException
import com.example.common.exceptions.RoomNotFoundException
import com.example.roomservice.repository.*
import com.example.roomservice.service.RoomAccountManagerImpl
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class RoomAccountManagerTest {

    private val roomRepository: RoomRepository = mock()
    private val accountInRoomRepository: AccountInRoomRepository = mock()
    private val bannedAccountInRoomRepository: BannedAccountInRoomRepository = mock()
    private val personalAccountClient: ReactivePersonalAccountClient = mock()
    private val roomAccountManager = RoomAccountManagerImpl(
        roomRepository,
        accountInRoomRepository,
        bannedAccountInRoomRepository,
        personalAccountClient
    )

    @Test
    fun `addAccount should add account to room when conditions are met`() {
        val roomId = 1L
        val accountId = 2L
        val room = RoomEntity(roomId, "Test Room", 1L, 5, 0)

        whenever(roomRepository.findById(roomId)).thenReturn(Mono.just(room))
        whenever(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        whenever(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())
        whenever(accountInRoomRepository.save(any())).thenReturn(Mono.just(AccountInRoomEntity(accountId, roomId)))
        whenever(
            personalAccountClient.updateAccountRoom(
                accountId,
                UpdateAccountRoomRequest(roomId)
            )
        ).thenReturn(Mono.empty())

        StepVerifier.create(roomAccountManager.addAccount(roomId, accountId))
            .expectComplete()
            .verify()

        verify(accountInRoomRepository).save(AccountInRoomEntity(accountId, roomId, true))
        verify(personalAccountClient).updateAccountRoom(accountId, UpdateAccountRoomRequest(roomId))
    }

    @Test
    fun `addAccount should return error when account is not requester`() {
        val roomId = 1L
        val accountId = 2L

        StepVerifier.create(roomAccountManager.addAccount(roomId, accountId))
            .expectError(ForbiddenOperationException::class.java)
            .verify()
    }

    @Test
    fun `addAccount should return error when room not found`() {
        val roomId = 1L
        val accountId = 2L

        whenever(roomRepository.findById(roomId)).thenReturn(Mono.empty())

        StepVerifier.create(roomAccountManager.addAccount(roomId, accountId))
            .expectError(RoomNotFoundException::class.java)
            .verify()
    }

    @Test
    fun `addAccount should return error when account is banned`() {
        val roomId = 1L
        val accountId = 2L
        val room = RoomEntity(roomId, "Test Room", 1L, 5, 0)

        whenever(roomRepository.findById(roomId)).thenReturn(Mono.just(room))
        whenever(bannedAccountInRoomRepository.findAllByRoomId(roomId)).thenReturn(
            Flux.just(
                BannedAccountInRoomEntity(
                    1,
                    accountId,
                    roomId
                )
            )
        )

        StepVerifier.create(roomAccountManager.addAccount(roomId, accountId))
            .expectError(ForbiddenOperationException::class.java)
            .verify()
    }

    @Test
    fun `removeAccount should remove account from room when conditions are met`() {
        val roomId = 1L
        val accountId = 2L
        val requesterId = 1L
        val room = RoomEntity(roomId, "Test Room", requesterId, 5, 0)
        whenever(roomRepository.findById(roomId)).thenReturn(Mono.just(room))
        whenever(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(
            Flux.just(
                AccountInRoomEntity(
                    accountId,
                    roomId
                )
            )
        )

        StepVerifier.create(roomAccountManager.removeAccount(roomId, accountId, AccountAction.KICK))
            .expectComplete()
            .verify()

        verify(accountInRoomRepository).findAllByRoomId(roomId)
    }

    @Test
    fun `removeAccount should return error when room not found`() {
        val roomId = 1L
        val accountId = 2L

        whenever(roomRepository.findById(roomId)).thenReturn(Mono.empty())

        StepVerifier.create(roomAccountManager.removeAccount(roomId, accountId, AccountAction.KICK))
            .expectError(RoomNotFoundException::class.java)
            .verify()
    }

    @Test
    fun `removeAccount should return error when account not found in room`() {
        val roomId = 1L
        val accountId = 2L
        val requesterId = 1L
        val room = RoomEntity(roomId, "Test Room", requesterId, 5, 0)

        whenever(roomRepository.findById(roomId)).thenReturn(Mono.just(room))
        whenever(accountInRoomRepository.findAllByRoomId(roomId)).thenReturn(Flux.empty())

        StepVerifier.create(roomAccountManager.removeAccount(roomId, accountId, AccountAction.KICK))
            .expectError(AccountNotFoundException::class.java)
            .verify()
    }
}