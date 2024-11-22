package com.example.gamehandlerservice.config

import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class AfterCloseWebSocketHandler(
    private val gameHandlerRegistry: GameHandlerRegistry
): TextWebSocketHandler() {
    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        super.afterConnectionClosed(session, closeStatus)
        val userId = session.attributes["x-user-id"] as Long
        val gameId = session.attributes["x-game-id"] as Long
        gameHandlerRegistry.getGame(gameId)?.playerDisconnect(userId)
    }
}
