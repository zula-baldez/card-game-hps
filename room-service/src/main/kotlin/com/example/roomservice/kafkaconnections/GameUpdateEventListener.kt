package com.example.roomservice.kafkaconnections

import com.example.common.dto.roomservice.AccountAction
import com.example.common.kafkaconnections.GameUpdateEvent
import com.example.common.kafkaconnections.GameUpdateEvent.Companion.GameUpdateEventType
import com.example.roomservice.service.RoomAccountManager
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import javax.security.sasl.AuthenticationException


@Service
class GameUpdateEventListener(
    private val roomAccountManager: RoomAccountManager
) {
    @KafkaListener(topics = ["game-connection-to-room-service"])
    fun listen(data: GameUpdateEvent) {
        if (data.eventType == GameUpdateEventType.PLAYER_DISCONNECT) {
            val event = data.playerDisconnect ?: throw IllegalArgumentException("broken message")
            try {
                val user: UsernamePasswordAuthenticationToken =
                    getUsernamePasswordAuthenticationToken(event.accountId)
                val authContext = ReactiveSecurityContextHolder.withAuthentication(user)
                roomAccountManager
                    .removeAccount(data.roomId, event.accountId, AccountAction.BAN)
                    .contextWrite(authContext)
                    .block()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    @Throws(AuthenticationException::class)
    fun getUsernamePasswordAuthenticationToken(uid: Long): UsernamePasswordAuthenticationToken {

        return UsernamePasswordAuthenticationToken(
            uid,
            null,
            listOf(GrantedAuthority { "USER" })
        )
    }
}
