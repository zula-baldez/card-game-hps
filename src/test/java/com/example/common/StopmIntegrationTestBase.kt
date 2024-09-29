package com.example.common

import com.example.authservice.service.UserService
import com.example.gamehandlerservice.model.exception.TimeoutException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutionException
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

class StopmIntegrationTestBase: E2EDbInit() {
    @Value("\${local.server.port}")
    private val port = 0

    private lateinit var receivedMessages: BlockingQueue<String>

    private lateinit var session: StompSession

    @Autowired
    lateinit var messagingTemplate: SimpMessagingTemplate
    @Autowired
    lateinit var userService: UserService
    @BeforeEach
    @Throws(
        InterruptedException::class,
        ExecutionException::class,
        TimeoutException::class
    )
    fun setup() {
        val token = userService.register("name", "password").token
        val URL = "ws://localhost:$port/app/game?roomId=123"
        val transportList: List<Transport> = listOf(WebSocketTransport(StandardWebSocketClient()))
        val stompClient = WebSocketStompClient(SockJsClient(transportList))
        stompClient.messageConverter = StringMessageConverter()
        val handshakeHeaders = WebSocketHttpHeaders()
        handshakeHeaders.add("Authorization", "Bearer $token")
        val connectHeaders = StompHeaders()
        connectHeaders.add("Authorization", "Bearer $token")
        receivedMessages = LinkedBlockingDeque()
        session = stompClient.connectAsync(URL, handshakeHeaders, connectHeaders, MySessionHandler())[5, SECONDS]
    }

    @Test
    @Throws(Exception::class)
    fun stompTest() {
        val message = "myMessage"
        messagingTemplate.convertAndSend(GAME_TOPIC, message)
        val response = receivedMessages.poll(5, SECONDS)
        assertEquals(message, response)
    }

    @AfterEach
    @Throws(InterruptedException::class)
    fun reset() {
        session.disconnect()
    }


    private inner class MySessionHandler : StompSessionHandlerAdapter() {
        override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
            session.subscribe(GAME_TOPIC, this)
        }

        override fun handleException(
            session: StompSession,
            command: StompCommand?,
            headers: StompHeaders,
            payload: ByteArray,
            exception: Throwable
        ) {
            LOGGER.warn("Stomp Error:", exception)
        }

        override fun handleTransportError(session: StompSession, exception: Throwable) {
            super.handleTransportError(session, exception)
            LOGGER.warn("Stomp Transport Error:", exception)
        }

        override fun getPayloadType(headers: StompHeaders): Type {
            return String::class.java
        }

        override fun handleFrame(stompHeaders: StompHeaders, o: Any?) {
            LOGGER.info("Handle Frame with payload {}", o)
            try {
                receivedMessages.offer(o as String?, 500, MILLISECONDS)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
    }

    companion object {
        private const val GAME_TOPIC = "/topic/test"
        private val LOGGER: Logger = LoggerFactory.getLogger(StopmIntegrationTestBase::class.java)
    }
}