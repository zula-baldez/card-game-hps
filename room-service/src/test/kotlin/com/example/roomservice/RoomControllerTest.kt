package com.example.roomservice

import com.example.common.dto.api.Pagination
import com.example.common.dto.roomservice.*
import com.example.common.exceptions.*
import com.example.roomservice.controllers.RoomController
import com.example.roomservice.service.RoomAccountManager
import com.example.roomservice.service.RoomManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal

class RoomControllerTest {

    private lateinit var roomManager: RoomManager
    private lateinit var roomAccountManager: RoomAccountManager
    private lateinit var roomController: RoomController

    @BeforeEach
    fun setup() {
        roomManager = mock()
        roomAccountManager = mock()
        roomController = RoomController(roomManager, roomAccountManager)
    }

    @Test
    fun `should return available rooms`() {
        val pagination = Pagination()
        val roomDtos = listOf(
            RoomDto(
                id = 1,
                name = "Room1",
                hostId = 1L,
                capacity = 10,
                players = listOf(),
                currentGameId = 1L,
                bannedPlayers = emptyList()
            )
        )
        whenever(roomManager.getRooms(pagination)).thenReturn(Flux.fromIterable(roomDtos))

        val result = roomController.getAvailableRooms(pagination).collectList().block()

        assertEquals(roomDtos, result)
    }

    @Test
    fun `should return room by ID`() {
        val roomId = 1L
        val roomDto = RoomDto(
            id = 1,
            name = "Room1",
            hostId = 1L,
            capacity = 10,
            players = listOf(),
            currentGameId = 1L,
            bannedPlayers = emptyList()
        )
        whenever(roomManager.getRoom(roomId)).thenReturn(Mono.just(roomDto))

        val result = roomController.getRoomById(roomId).block()

        assertEquals(roomDto, result)

    }

    @Test
    fun `should throw RoomNotFoundException when room not found`() {
        val roomId = 1L
        whenever(roomManager.getRoom(roomId)).thenReturn(Mono.empty())

        assertThrows<RoomNotFoundException> {
            roomController.getRoomById(roomId).block()
        }

    }

    @Test
    fun `should create a room`() {
        val createRoomRequest = CreateRoomRequest(name = "Room1", capacity = 10)
        val principal = mock<Principal> { on { name } doReturn "123" }
        val createdRoomDto = RoomDto(
            id = 1,
            name = "Room1",
            hostId = 123L,
            capacity = 10,
            players = listOf(),
            currentGameId = 1L,
            bannedPlayers = emptyList()
        )


        whenever(roomManager.createRoom(createRoomRequest.name, 123L, createRoomRequest.capacity))
            .thenReturn(Mono.just(createdRoomDto))

        val result = roomController.createRoom(createRoomRequest, principal).block()

        assertEquals(createdRoomDto, result)

    }

    @Test
    fun `should add a player to a room`() {
        val roomId = 1L
        val addAccountRequest = AddAccountRequest(accountId = 2L)

        whenever(roomAccountManager.addAccount(roomId, addAccountRequest.accountId)).thenReturn(Mono.empty())

        val result = roomController.addPlayer(roomId, addAccountRequest).block()

        assertEquals(null, result)
    }

    @Test
    fun `should remove a player from a room`() {
        val roomId = 1L
        val accountId = 2L
        val removeAccountRequest = RemoveAccountRequest(reason = AccountAction.KICK)
        val principal = mock<Principal> { on { name } doReturn "123" }

        whenever(
            roomAccountManager.removeAccount(
                roomId,
                accountId,
                removeAccountRequest.reason
            )
        ).thenReturn(Mono.empty())

        val result = roomController.removePlayer(roomId, accountId, removeAccountRequest, principal).block()

        assertEquals(null, result)
    }

    @Test
    fun `should return NOT FOUND status when RoomNotFoundException is thrown`() {
        val exception = RoomNotFoundException(1)
        val response = roomController.handleRoomNotFoundException(exception)

        assertEquals("Room with id 1 not found", response)
        assertEquals(HttpStatus.NOT_FOUND, getHttpStatus(RoomNotFoundException::class.java))
    }

    @Test
    fun `should return NOT FOUND status when AccountNotFoundException is thrown`() {
        val exception = AccountNotFoundException(1)
        val response = roomController.handleAccountNotFoundException(exception)

        assertEquals("Account with id 1 not found", response)
        assertEquals(HttpStatus.NOT_FOUND, getHttpStatus(AccountNotFoundException::class.java))
    }

    @Test
    fun `should return BAD REQUEST status when HostOnlyException is thrown`() {
        val exception = HostOnlyException()
        val response = roomController.handleAccountNotFoundException(exception)

        assertEquals("This operation is host only", response)
        assertEquals(HttpStatus.BAD_REQUEST, getHttpStatus(HostOnlyException::class.java))
    }

    @Test
    fun `should return BAD REQUEST status when RoomOverflowException is thrown`() {
        val exception = RoomOverflowException(1)
        val response = roomController.handleRoomOverflowException(exception)

        assertEquals("Room with id 1 is overflow", response)
        assertEquals(HttpStatus.BAD_REQUEST, getHttpStatus(RoomOverflowException::class.java))
    }

    @Test
    fun `should return FORBIDDEN status when ForbiddenOperationException is thrown`() {
        val exception = ForbiddenOperationException()
        val response = roomController.handleForbiddenOperationException(exception)

        assertEquals("Access denied", response)
        assertEquals(HttpStatus.FORBIDDEN, getHttpStatus(ForbiddenOperationException::class.java))
    }
    private fun getHttpStatus(exceptionClass: Class<out Exception>): HttpStatus {
        return when (exceptionClass) {
            RoomNotFoundException::class.java, AccountNotFoundException::class.java -> HttpStatus.NOT_FOUND
            HostOnlyException::class.java, RoomOverflowException::class.java -> HttpStatus.BAD_REQUEST
            ForbiddenOperationException::class.java -> HttpStatus.FORBIDDEN
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}