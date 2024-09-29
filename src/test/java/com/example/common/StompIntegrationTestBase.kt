package com.example.common

import com.example.authservice.service.UserService
import com.example.roomservice.service.RoomManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
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
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

class StompIntegrationTestBase : E2EDbInit() {
    @Value("\${local.server.port}")
    private val port = 0
    private val stompSession = mutableListOf<StompSession>()
    private lateinit var receivedMessages: MutableMap<Long, BlockingQueue<String>>

    @Autowired
    lateinit var messagingTemplate: SimpMessagingTemplate

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var roomManager: RoomManager


    fun getClientStompSession(roomId: Long, userId: Long, token: String): StompSession {
        val url = "ws://localhost:$port/app/game?roomId=$roomId"
        val transportList: List<Transport> = listOf(WebSocketTransport(StandardWebSocketClient()))
        val stompClient = WebSocketStompClient(SockJsClient(transportList))
        stompClient.messageConverter = StringMessageConverter()
        val handshakeHeaders = WebSocketHttpHeaders()
        handshakeHeaders.add("Authorization", "Bearer $token")
        val connectHeaders = StompHeaders()
        connectHeaders.add("Authorization", "Bearer $token")
        receivedMessages[userId] = LinkedBlockingDeque()
        val session = stompClient.connectAsync(
            url,
            handshakeHeaders,
            connectHeaders,
            GameClientSessionHandler(userId)
        )[5, SECONDS]
        stompSession += session
        return session
    }

    fun getMessage(userId: Long): String? {
        return receivedMessages[userId]?.poll()
    }

    @AfterEach
    fun closeSessions() {
        receivedMessages.clear()
        stompSession.forEach { stompSession -> stompSession.disconnect() }
    }

    @Test
    @Throws(Exception::class)
    fun stompTest() {
        val message = "myMessage"
        val user = userService.register("name", "pass")
        val room = roomManager.createRoom("room", user.id, 10)
        var session = getClientStompSession(room.id, user.id, user.token)
        messagingTemplate.convertAndSend(TEST_TOPIC, message)
        val response = receivedMessages[user.id]?.poll(5, SECONDS)
        assertEquals(message, response)
    }

    private inner class GameClientSessionHandler(private val userId: Long) : StompSessionHandlerAdapter() {
        override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
            session.subscribe(TEST_TOPIC, this)
            session.subscribe(TEST_TOPIC, this)
            session.subscribe(TEST_TOPIC, this)
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
                receivedMessages[userId]?.offer(o as String?, 500, MILLISECONDS)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
    }

    companion object {
        private const val TEST_TOPIC = "/topic/test"
        private val LOGGER: Logger = LoggerFactory.getLogger(StompIntegrationTestBase::class.java)
    }
}