package com.example.roomservice.controllers

import com.example.common.dto.api.Pagination
import com.example.common.dto.roomservice.AddAccountRequest
import com.example.common.dto.roomservice.CreateRoomRequest
import com.example.common.dto.roomservice.RemoveAccountRequest
import com.example.common.dto.roomservice.RoomDto
import com.example.common.exceptions.AccountNotFoundException
import com.example.common.exceptions.ForbiddenOperationException
import com.example.common.exceptions.HostOnlyException
import com.example.common.exceptions.RoomNotFoundException
import com.example.common.exceptions.RoomOverflowException
import com.example.roomservice.service.RoomAccountManager
import com.example.roomservice.service.RoomManager
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@Validated
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "room_controller", description = "Rest API for room service")
class RoomController(
    private val roomManager: RoomManager,
    private val roomAccountManger: RoomAccountManager
) {
    @GetMapping("/rooms")
    @Operation(summary = "Get all rooms")
    fun getAvailableRooms(@Valid page: Pagination?): Flux<RoomDto> {
        return roomManager.getRooms(page ?: Pagination())
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "Get room by id")
    fun getRoomById(@PathVariable roomId: Long): Mono<RoomDto> {
        return roomManager.getRoom(roomId).switchIfEmpty(Mono.error(RoomNotFoundException(roomId)))
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Create room")
    fun createRoom(@RequestBody @Valid createRoomRequest: CreateRoomRequest, principal: Principal): Mono<RoomDto> {
        return roomManager.createRoom(
            createRoomRequest.name,
            principal.name.toLong(),
            createRoomRequest.capacity
        )
    }

    @PostMapping("/rooms/{roomId}/players")
    @PreAuthorize("hasAuthority('SCOPE_USER') and (authentication.name == #addAccountRequest.accountId.toString() or hasAuthority('SCOPE_ADMIN'))")
    @Operation(summary = "Add player to room")
    fun addPlayer(@PathVariable roomId: Long, @RequestBody @Valid addAccountRequest: AddAccountRequest): Mono<Void> {
        return roomAccountManger.addAccount(roomId, addAccountRequest.accountId)
    }

    @DeleteMapping("/rooms/{roomId}/players/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@roomSecurityUtils.canRemoveAccount(#roomId, authentication, #accountId)")
    @Operation(summary = "Remove player from room")
    fun removePlayer(
        @PathVariable roomId: Long,
        @PathVariable accountId: Long,
        @RequestBody @Valid removeAccountRequest: RemoveAccountRequest,
        principal: Principal
    ): Mono<Void> {
        return roomAccountManger.removeAccount(roomId, accountId, removeAccountRequest.reason)
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
