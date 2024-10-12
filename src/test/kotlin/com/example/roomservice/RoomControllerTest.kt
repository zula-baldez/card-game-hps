package com.example.roomservice

import com.example.common.dto.api.ScrollPositionDto
import com.example.common.dto.business.RoomDto
import com.example.gamehandlerservice.model.dto.AccountAction
import com.example.roomservice.controllers.RoomController
import com.example.roomservice.dto.AddAccountRequest
import com.example.roomservice.dto.CreateRoomRequest
import com.example.roomservice.dto.RemoveAccountRequest
import com.example.roomservice.service.RoomAccountManager
import com.example.roomservice.service.RoomManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.Mock
import org.mockito.Mockito.*
import java.security.Principal

class RoomControllerTest {

    @Mock
    private lateinit var roomManager: RoomManager

    @Mock
    private lateinit var roomAccountManager: RoomAccountManager

    private lateinit var roomController: RoomController

    @BeforeEach
    fun setUp() {
        roomManager = mock(com.example.roomservice.service.RoomManagerImpl::class.java)
        roomAccountManager = mock(com.example.roomservice.service.RoomAccountManagerImpl::class.java)
        roomController = RoomController(roomManager, roomAccountManager)
    }

    @Test
    fun `should return all available rooms`() {
        val scrollPositionDto = ScrollPositionDto(10)
        val roomDtoList: List<RoomDto> = listOf(RoomDto(1L, "Room1", 10, 2, mutableListOf(), 1L, mutableListOf()))

        `when`(roomManager.getAllRooms(scrollPositionDto)).thenReturn(roomDtoList)

        val result = roomController.getAvailableRooms(scrollPositionDto)

        assertEquals(roomDtoList, result)
        verify(roomManager).getAllRooms(scrollPositionDto)
    }

    @Test
    fun `should return room by ID`() {
        val roomId = 1L
        val roomDto = RoomDto(1L, "Room1", 10, 2, mutableListOf(), 1L, mutableListOf())

        `when`(roomManager.getRoom(roomId)).thenReturn(roomDto)

        val result = roomController.getRoomById(roomId)

        assertEquals(roomDto, result)
        verify(roomManager).getRoom(roomId)
    }

    @Test
    fun `should create room`() {
        val createRoomRequest = CreateRoomRequest(5, "New Room")
        val principal: Principal = mock(Principal::class.java)
        `when`(principal.name).thenReturn("1")
        val roomDto = RoomDto(1L, "Room1", 5, 2, mutableListOf(), 1L, mutableListOf())

        `when`(roomManager.createRoom("New Room", 1L, 5)).thenReturn(roomDto)

        val result = roomController.createRoom(createRoomRequest, principal)

        assertEquals(roomDto, result)
        verify(roomManager).createRoom("New Room", 1L, 5)
    }
    @Test
    fun `addPlayer should add player to the room`() {
        val roomId = 1L
        val addAccountRequest = AddAccountRequest(accountId = 123L)
        roomController.addPlayer(roomId, addAccountRequest)
        verify(roomAccountManager).addAccount(roomId, addAccountRequest.accountId)
    }
    @Test
    fun `removePlayer should remove player from the room`() {
        val roomId = 1L
        val accountId = 123L
        val removeAccountRequest = RemoveAccountRequest(reason = AccountAction.LEAVE)
        roomController.removePlayer(roomId, accountId, removeAccountRequest)
        verify(roomAccountManager).removeAccount(roomId, accountId, removeAccountRequest.reason)
    }
}