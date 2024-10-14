package com.example.roomservice.controllers

import com.example.common.dto.api.ScrollPositionDto
import com.example.common.dto.business.RoomDto
import com.example.common.exceptions.*
import com.example.roomservice.dto.AddAccountRequest
import com.example.roomservice.dto.CreateRoomRequest
import com.example.roomservice.dto.RemoveAccountRequest
import com.example.roomservice.service.RoomAccountManager
import com.example.roomservice.service.RoomManager
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class RoomController(
    private val roomManager: RoomManager,
    private val roomAccountManger: RoomAccountManager
) {
    @GetMapping("/rooms")
    fun getAvailableRooms(@Valid scrollPositionDto: ScrollPositionDto): List<RoomDto> {
        return roomManager.getAllRooms(scrollPositionDto)
    }

    @GetMapping("/rooms/{roomId}")
    fun getRoomById(@PathVariable roomId: Long): RoomDto {
        return roomManager.getRoom(roomId) ?: throw RoomNotFoundException(roomId)
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRoom(@RequestBody @Valid createRoomRequest: CreateRoomRequest, @RequestHeader("x-user-id") userId: Long): RoomDto {
        return roomManager.createRoom(
            createRoomRequest.name,
            userId,
            createRoomRequest.capacity
        )
    }

    @PostMapping("/rooms/{roomId}/players")
    fun addPlayer(@PathVariable roomId: Long, @RequestBody @Valid addAccountRequest: AddAccountRequest, @RequestHeader("x-user-id") userId: Long) {
        return roomAccountManger.addAccount(roomId, addAccountRequest.accountId, userId)
    }

    @DeleteMapping("/rooms/{roomId}/players/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removePlayer(
        @PathVariable roomId: Long,
        @PathVariable accountId: Long,
        @RequestBody @Valid removeAccountRequest: RemoveAccountRequest,
        @RequestHeader("x-user-id") requesterId: Long
    ) {
        return roomAccountManger.removeAccount(roomId, accountId, removeAccountRequest.reason, requesterId)
    }

    @ExceptionHandler(RoomNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleRoomNotFoundException(ex: RoomNotFoundException): String {
        return ex.message ?: "Room not found"
    }

    @ExceptionHandler(AccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleAccountNotFoundException(ex: AccountNotFoundException): String {
        return ex.message ?: "Account not found"
    }

    @ExceptionHandler(HostOnlyException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleAccountNotFoundException(ex: HostOnlyException): String {
        return ex.message ?: "This operation is host only"
    }

    @ExceptionHandler(RoomOverflowException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRoomOverflowException(ex: RoomOverflowException): String {
        return ex.message ?: "Room overflow exception"
    }

    @ExceptionHandler(ForbiddenOperationException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbiddenOperationException(ex: ForbiddenOperationException): String {
        return ex.message ?: "Forbidden"
    }
}
