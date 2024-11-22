package com.example.gamehandlerservice.config

import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(private val gameHandlerRegistry: GameHandlerRegistry): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
            registry.addHandler(AfterCloseWebSocketHandler(gameHandlerRegistry), "/app/game").setAllowedOrigins("*")
        }
    }
}