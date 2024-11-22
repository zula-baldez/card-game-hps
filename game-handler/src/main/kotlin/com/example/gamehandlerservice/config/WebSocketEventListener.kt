package com.example.gamehandlerservice.config

import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.springframework.context.ApplicationListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import javax.security.sasl.AuthenticationException


@Component
class WebSocketEventListener(
    private val gameHandlerRegistry: GameHandlerRegistry
) : ApplicationListener<SessionDisconnectEvent> {
    override fun onApplicationEvent(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val accountId = headerAccessor.sessionAttributes?.get("x-user-id") as Long
        val gameId = headerAccessor.sessionAttributes?.get("x-game-id") as Long
        val user: UsernamePasswordAuthenticationToken =
            getUsernamePasswordAuthenticationToken(accountId)

        SecurityContextHolder.getContext().authentication = user
        gameHandlerRegistry.getGame(gameId)?.playerDisconnect(accountId)
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
