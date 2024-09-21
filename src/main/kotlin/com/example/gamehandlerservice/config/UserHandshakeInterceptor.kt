package com.example.gamehandlerservice.config

import com.example.personalaccount.database.Account
import com.example.personalaccount.database.AccountRepo
import com.example.gamehandlerservice.security.TokenParser
import com.example.roomservice.service.RoomManager
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.util.UriComponentsBuilder

@Component
class UserHandshakeInterceptor(
    private val roomManager: RoomManager, private val accountRepo: AccountRepo, private val tokenParser: TokenParser
) : HandshakeInterceptor {
    @Throws(Exception::class)
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String?, Any?>
    ): Boolean {
        try {
            val uriString = request.uri.toString()
            val roomId = requireNotNull(
                UriComponentsBuilder.fromUriString(uriString).build().queryParams.getFirst("roomId")
            )
            val token = requireNotNull(extractJwtTokenFromRequest(request))
            val userId = requireNotNull(tokenParser.getIdFromToken(token))
            val account = accountRepo.findById(userId.toLong()).orElseGet {
                val newAccount = Account("", 0, true, 0, userId.toLong())
                accountRepo.save(newAccount)
            }
            account.active = true
            val roomHandler = roomManager.getRoom(roomId.toLong())
            roomHandler?.addAccount(account)
            attributes["room"] = roomManager.getRoom(roomId.toLong())
            attributes["account"] = account
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, exception: Exception?
    ) {
    }

    private fun extractJwtTokenFromRequest(request: ServerHttpRequest): String? =
        request.headers["Authorization"]?.firstOrNull()?.removePrefix("Bearer ")
}