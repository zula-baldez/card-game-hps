package com.example.roomservice.security

import com.example.roomservice.service.RoomManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RoomSecurityUtils(
    private val roomManager: RoomManager
) {
    fun canRemoveAccount(roomId: Long, authentication: Authentication, removeAccount: Long): Mono<Boolean> {
        if (authentication.authorities.find { it.authority == "SCOPE_USER" } == null) {
            return Mono.just(false)
        }

        if (authentication.authorities.find { it.authority == "SCOPE_ADMIN" } != null) {
            return Mono.just(true)
        }

        if (authentication.name == removeAccount.toString()) {
            return Mono.just(true)
        }

        return roomManager
            .getRoom(roomId)
            .map { room ->
                return@map room.hostId.toString() == authentication.name
            }
    }
}