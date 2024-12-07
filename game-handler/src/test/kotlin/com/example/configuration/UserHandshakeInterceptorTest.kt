package com.example.configuration

import com.example.gamehandlerservice.config.UserHandshakeInterceptor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.util.UriComponentsBuilder

class UserHandshakeInterceptorTest {

    private val userHandshakeInterceptor = UserHandshakeInterceptor()

    @Test
    fun `should return true and set roomId in attributes when roomId is present in query params`() {
        val roomId = 123L
        val request = mock(ServerHttpRequest::class.java)
        val response = mock(ServerHttpResponse::class.java)
        val wsHandler = mock(WebSocketHandler::class.java)
        val attributes = mutableMapOf<String?, Any?>()

        `when`(request.uri).thenReturn(UriComponentsBuilder.fromUriString("ws://localhost:8080/websocket?roomId=$roomId").build().toUri())

        val result = userHandshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes)

        assertTrue(result)
        assertEquals(roomId, attributes["x-room-id"])
    }

    @Test
    fun `should return false when roomId is not present in query params`() {
        val request = mock(ServerHttpRequest::class.java)
        val response = mock(ServerHttpResponse::class.java)
        val wsHandler = mock(WebSocketHandler::class.java)
        val attributes = mutableMapOf<String?, Any?>()

        `when`(request.uri).thenReturn(UriComponentsBuilder.fromUriString("ws://localhost:8080/websocket").build().toUri())

        val result = userHandshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes)

        assertFalse(result)
        assertEquals(0, attributes.size)
    }

    @Test
    fun `should return false when roomId is not a valid long`() {
        val request = mock(ServerHttpRequest::class.java)
        val response = mock(ServerHttpResponse::class.java)
        val wsHandler = mock(WebSocketHandler::class.java)
        val attributes = mutableMapOf<String?, Any?>()

        `when`(request.uri).thenReturn(UriComponentsBuilder.fromUriString("ws://localhost:8080/websocket?roomId=invalid").build().toUri())

        val result = userHandshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes)

        assertFalse(result)
        assertEquals(0, attributes.size)
    }
}