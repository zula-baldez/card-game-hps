package com.example.roomservice.controllers

import com.example.common.dto.api.Pagination
import com.example.common.dto.roomservice.RoomDto
import com.example.common.exceptions.*
import com.example.common.dto.roomservice.AddAccountRequest
import com.example.common.dto.roomservice.CreateRoomRequest
import com.example.common.dto.roomservice.RemoveAccountRequest
import com.example.roomservice.service.RoomAccountManager
import com.example.roomservice.service.RoomManager
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@Validated
class RoomController(
    private val roomManager: RoomManager,
    private val roomAccountManger: RoomAccountManager
) {
    @GetMapping("/rooms")
    fun getAvailableRooms(@Valid page: Pagination?): Flux<RoomDto> {
        return roomManager.getRooms(page ?: Pagination())
    }

    @GetMapping("/rooms/{roomId}")
    fun getRoomById(@PathVariable roomId: Long): Mono<RoomDto> {
        return roomManager.getRoom(roomId).switchIfEmpty(Mono.error(RoomNotFoundException(roomId)))
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRoom(@RequestBody @Valid createRoomRequest: CreateRoomRequest, @RequestHeader("x-user-id") userId: Long): Mono<RoomDto> {
        return roomManager.createRoom(
            createRoomRequest.name,
            userId,
            createRoomRequest.capacity
        )
    }

    @PostMapping("/rooms/{roomId}/players")
    fun addPlayer(@PathVariable roomId: Long, @RequestBody @Valid addAccountRequest: AddAccountRequest, @RequestHeader("x-user-id") userId: Long): Mono<Void> {
        return roomAccountManger.addAccount(roomId, addAccountRequest.accountId, userId)
    }

    @DeleteMapping("/rooms/{roomId}/players/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removePlayer(
        @PathVariable roomId: Long,
        @PathVariable accountId: Long,
        @RequestBody @Valid removeAccountRequest: RemoveAccountRequest,
        @RequestHeader("x-user-id") userId: Long
    ): Mono<Void> {
        return roomAccountManger.removeAccount(roomId, accountId, removeAccountRequest.reason, userId)
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
