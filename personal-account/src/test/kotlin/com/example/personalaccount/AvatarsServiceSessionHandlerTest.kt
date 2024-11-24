package com.example.personalaccount

import com.example.personalaccount.service.AvatarsServiceSessionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession

class AvatarsServiceSessionHandlerTest {

    private lateinit var sessionHandler: AvatarsServiceSessionHandler
    private lateinit var mockSession: StompSession
    private lateinit var mockHeaders: StompHeaders

    @BeforeEach
    fun setUp() {
        sessionHandler = AvatarsServiceSessionHandler()
        mockSession = mock(StompSession::class.java)
        mockHeaders = mock(StompHeaders::class.java)
    }

    @Test
    fun testAfterConnected() {
        sessionHandler.afterConnected(mockSession, mockHeaders)
    }

    @Test
    fun testHandleException() {
        val exception = Exception("Test Exception")
        sessionHandler.handleException(mockSession, null, mockHeaders, ByteArray(0), exception)
    }

    @Test
    fun testHandleTransportError() {
        val exception = Exception("Transport Error")

        sessionHandler.handleTransportError(mockSession, exception)

    }

    @Test
    fun testHandleFrame() {
        val payload = Any()
        sessionHandler.handleFrame(mockHeaders, payload)

    }
}