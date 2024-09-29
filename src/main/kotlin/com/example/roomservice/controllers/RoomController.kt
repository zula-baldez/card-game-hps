package com.example.roomservice.controllers

import com.example.common.dto.api.ScrollPositionDto
import com.example.roomservice.dto.CreateRoomRequest
import com.example.common.dto.business.RoomDto
import com.example.roomservice.dto.AddAccountRequest
import com.example.roomservice.dto.RemoveAccountRequest
import com.example.roomservice.dto.RoomAccountActionResult
import com.example.roomservice.service.RoomAccountManager
import com.example.roomservice.service.RoomManager
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class RoomController(
    private val roomManager: RoomManager,
    private val roomAccountManger: RoomAccountManager
) {
    @GetMapping("/rooms")
    fun getAvailableRooms(scrollPositionDto: ScrollPositionDto): List<RoomDto> {
        return roomManager.getAllRooms(scrollPositionDto)
    }

    @GetMapping("/rooms/{roomId}")
    fun getRoomById(@PathVariable roomId: Long): RoomDto? {
        return roomManager.getRoom(roomId)
    }

    @PostMapping("/rooms")
    fun createRoom(@RequestBody createRoomRequest: CreateRoomRequest, principal: Principal): RoomDto {
        return roomManager.createRoom(
            createRoomRequest.name,
            principal.name.toLong(),
            createRoomRequest.capacity
        )
    }

    @PostMapping("/rooms/{roomId}/players")
    fun addPlayer(@PathVariable roomId: Long, @RequestBody addAccountRequest: AddAccountRequest): RoomAccountActionResult {
        return roomAccountManger.addAccount(roomId, addAccountRequest.accountId)
    }

    @DeleteMapping("/rooms/{roomId}/players/{accountId}")
    fun removePlayer(@PathVariable roomId: Long, @PathVariable accountId: Long, @RequestBody removeAccountRequest: RemoveAccountRequest): RoomAccountActionResult {
        return roomAccountManger.removeAccount(roomId, accountId, removeAccountRequest.reason)
    }
}