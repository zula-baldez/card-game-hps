package com.example

import com.example.configuration.TestConfiguration
import com.example.gamehandlerservice.model.dto.MoveCardResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.TestPropertySource
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.Thread.sleep
import java.lang.reflect.Type
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

@ExtendWith(MockitoExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["spring.config.location=classpath:application.yaml"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestConfiguration::class)
class StompIntegrationTestBase {
    @Value("\${local.server.port}")
    private val port = 0
    private val stompSession = mutableListOf<StompSession>()
    private var receivedMessages: MutableMap<Long, BlockingQueue<MoveCardResponse>> = mutableMapOf()

    @Autowired
    lateinit var messagingTemplate: SimpMessagingTemplate


    fun getClientStompSession(roomId: Long, userId: Long, token: String): StompSession {
        val url = "ws://localhost:$port/app/game?roomId=$roomId"
        val transportList: List<Transport> = listOf(WebSocketTransport(StandardWebSocketClient()))
        val stompClient = WebSocketStompClient(SockJsClient(transportList))
        stompClient.messageConverter = MappingJackson2MessageConverter()
        val handshakeHeaders = WebSocketHttpHeaders()
        handshakeHeaders.add("Authorization", "Bearer $token")
        receivedMessages[userId] = LinkedBlockingDeque()
        val session = stompClient.connectAsync(
            url,
            handshakeHeaders,
            StompHeaders(),
            GameClientSessionHandler(userId)
        )[5, SECONDS]
        stompSession += session
        sleep(1000)
        return session
    }

    fun getMessage(userId: Long): MoveCardResponse? {
        return receivedMessages[userId]?.poll(1000, MILLISECONDS)
    }

    @AfterEach
    fun closeSessions() {
        receivedMessages.clear()
        stompSession.forEach { stompSession ->
            try {
                stompSession.disconnect()
            } catch (_: Exception) {}
        }
    }

    private inner class GameClientSessionHandler(private val userId: Long) : StompSessionHandlerAdapter() {
        override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
            session.subscribe(TEST_TOPIC, this)
            session.subscribe("/topic/card-changes", this)
            session.subscribe("/topic/accounts", this)
            session.subscribe("/topic/common/players", this)
            session.subscribe("/topic/start-game", this)

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
            return MoveCardResponse::class.java
        }

        override fun handleFrame(stompHeaders: StompHeaders, o: Any?) {
            LOGGER.info("Handle Frame with payload {}", o)
            try {
                receivedMessages[userId]?.offer(o as MoveCardResponse, 1000, MILLISECONDS)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
    }

    companion object {
        private const val TEST_TOPIC = "/topic/game"
        private val LOGGER: Logger = LoggerFactory.getLogger(StompIntegrationTestBase::class.java)
    }
}