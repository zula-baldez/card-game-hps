package com.example.configuration

import com.example.common.kafkaconnections.GameUpdateEvent
import com.example.gamehandlerservice.config.WebSocketEventListener
import com.example.gamehandlerservice.kafkaconnections.KafkaGameUpdateEventSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.springframework.messaging.Message
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.socket.messaging.SessionDisconnectEvent

class WebSocketEventListenerTest {

    private val disconnectEventSender: KafkaGameUpdateEventSender = mock(KafkaGameUpdateEventSender::class.java)
    private val webSocketEventListener = WebSocketEventListener(disconnectEventSender)

    @Test
    fun `should set security context with username password authentication token`() {
        val accountId = 123L
        val token = webSocketEventListener.getUsernamePasswordAuthenticationToken(accountId)

        assertNotNull(token)
        assertEquals(accountId, token.principal)
        assertEquals(1, token.authorities.size)
        assertEquals("USER", token.authorities.first().authority)

        SecurityContextHolder.getContext().authentication = token
        assertEquals(token, SecurityContextHolder.getContext().authentication)
    }
}