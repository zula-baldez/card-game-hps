package com.example.gamehandlerservice.config

import com.example.common.security.TokenParser
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.service.RoomManager
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.util.UriComponentsBuilder
import kotlin.jvm.optionals.getOrNull

@Component
class UserHandshakeInterceptor(
    private val roomManager: RoomManager,
    private val accountRepository: AccountRepository,
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

        val token = extractJwtTokenFromRequest(request) ?: return false
        val userId = tokenParser.getIdFromToken(token) ?: return false

        val account = accountRepository.findById(userId.toLong()).getOrNull() ?: return false

        val room = roomManager.getRoom(roomId) ?: return false
        if (room.bannedPlayers.contains(account.toDto())) {
            return false
        }
        attributes["roomId"] = roomId
        attributes["gameId"] = room.currentGameId
        attributes["accountId"] = account.id
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, exception: Exception?
    ) {
    }

    private fun extractJwtTokenFromRequest(request: ServerHttpRequest): String? =
        request.headers["Authorization"]?.firstOrNull()?.removePrefix("Bearer ")
}