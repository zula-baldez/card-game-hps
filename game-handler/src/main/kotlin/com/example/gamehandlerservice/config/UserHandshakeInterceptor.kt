package com.example.gamehandlerservice.config

import com.example.common.client.PersonalAccountClient
import com.example.common.client.RoomServiceClient
import com.example.common.security.TokenParser
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.util.UriComponentsBuilder

@Component
class UserHandshakeInterceptor(
    private val roomManager: RoomServiceClient,
    private val accountClient: PersonalAccountClient,
    private val tokenParser: TokenParser
) : HandshakeInterceptor {
    @Throws(Exception::class)
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String?, Any?>
    ): Boolean {
        val roomId = UriComponentsBuilder.fromUriString(request.uri.toString()).build()
            .queryParams.getFirst("roomId")?.toLongOrNull() ?: return false

        attributes["x-room-id"] = roomId
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, exception: Exception?
    ) {
    }
}