package com.example.roomservice

import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.dto.api.Pagination
import com.example.common.dto.personalaccout.AccountDto
import com.example.roomservice.repository.RoomEntity
import com.example.roomservice.repository.RoomRepository
import com.example.roomservice.service.RoomAccountManager
import com.example.roomservice.service.RoomManagerImpl
import com.example.roomservice.service.RoomUpdateEventSender
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class RoomManagerTest {

    private val roomRepository: RoomRepository = mock()
    private val roomAccountManager: RoomAccountManager = mock()
    private val personalAccountClient: ReactivePersonalAccountClient = mock()
    private val roomUpdateEventSender: RoomUpdateEventSender = mock()
    private val roomManager =
        RoomManagerImpl(roomRepository, roomAccountManager, personalAccountClient, roomUpdateEventSender)

    @Test
    fun `createRoom should create and return RoomDto`() {
        val roomName = "Test Room"
        val hostId = 1L
        val capacity = 5
        val roomEntity = RoomEntity(0, roomName, hostId, capacity, 0)
        val savedRoomEntity = RoomEntity(1, roomName, hostId, capacity, 0)

        whenever(roomRepository.save(roomEntity)).thenReturn(Mono.just(savedRoomEntity))

        val players = listOf(AccountDto(id = 2L, name = "Player1", fines = 0, "avatar", roomId = 1L))
        val bannedPlayers = listOf(AccountDto(id = 3L, name = "BannedPlayer", fines = 0, "avatar", roomId = 1L))

        whenever(roomAccountManager.getAccountsInRoom(savedRoomEntity.id)).thenReturn(Flux.just(2L))
        whenever(personalAccountClient.getAccountById(2L)).thenReturn(Mono.just(players[0]))

        whenever(roomAccountManager.getBannedAccountsInRoom(savedRoomEntity.id)).thenReturn(Flux.just(3L))
        whenever(personalAccountClient.getAccountById(3L)).thenReturn(Mono.just(bannedPlayers[0]))

        val result = roomManager.createRoom(roomName, hostId, capacity).block()

        assertNotNull(result)
        assertEquals(savedRoomEntity.id, result?.id)
        assertEquals(roomName, result?.name)
        assertEquals(capacity, result?.capacity)
        assertEquals(players, result?.players)
        assertEquals(bannedPlayers, result?.bannedPlayers)
    }

    @Test
    fun `deleteRoom should call repository deleteById`() {
        val roomId = 1L
        whenever(roomRepository.deleteById(roomId)).thenReturn(Mono.empty())

        roomManager.deleteRoom(roomId).block()
        verify(roomRepository).deleteById(roomId)
    }

    @Test
    fun `getRoom should return RoomDto when found`() {
        val roomId = 1L
        val roomEntity = RoomEntity(roomId, "Test Room", 1L, 5, 0)

        whenever(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity))

        val players = listOf(AccountDto(id = 2L, name = "Player1", fines = 0, "avatar", roomId = 1L))
        val bannedPlayers = listOf(AccountDto(id = 3L, name = "BannedPlayer", fines = 0, "avatar", roomId = 1L))

        whenever(roomAccountManager.getAccountsInRoom(roomId)).thenReturn(Flux.just(2L))
        whenever(personalAccountClient.getAccountById(2L)).thenReturn(Mono.just(players[0]))

        whenever(roomAccountManager.getBannedAccountsInRoom(roomId)).thenReturn(Flux.just(3L))
        whenever(personalAccountClient.getAccountById(3L)).thenReturn(Mono.just(bannedPlayers[0]))

        val result = roomManager.getRoom(roomId).block()

        assertNotNull(result)
        assertEquals(roomEntity.id, result?.id)
    }

    @Test
    fun `getRooms should return a list of RoomDto`() {
        val pagination = Pagination()
        val roomEntities = listOf(
            RoomEntity(1, "Room1", 1L, 5, 0),
            RoomEntity(2, "Room2", 2L, 10, 0)
        )

        whenever(roomRepository.findAllByIdNotNull(any())).thenReturn(Flux.fromIterable(roomEntities))
        whenever(roomAccountManager.getAccountsInRoom(1)).thenReturn(Flux.just(2L))
        whenever(personalAccountClient.getAccountById(2L)).thenReturn(
            Mono.just(
                AccountDto(
                    id = 2L,
                    name = "Player1",
                    fines = 0,
                    "avatar",
                    roomId = 1L
                )
            )
        )

        whenever(roomAccountManager.getBannedAccountsInRoom(1)).thenReturn(Flux.empty())

        whenever(roomAccountManager.getAccountsInRoom(2)).thenReturn(Flux.empty())
        whenever(roomAccountManager.getBannedAccountsInRoom(2)).thenReturn(Flux.empty())

        val result = roomManager.getRooms(pagination).collectList().block()

        assertNotNull(result)
        assertEquals(2, result?.size)
    }
}
