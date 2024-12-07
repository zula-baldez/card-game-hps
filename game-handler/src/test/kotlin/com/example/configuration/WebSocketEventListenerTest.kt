package com.example.configuration

import com.example.gamehandlerservice.config.WebSocketEventListener
import com.example.gamehandlerservice.kafkaconnections.KafkaGameUpdateEventSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.security.core.context.SecurityContextHolder

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