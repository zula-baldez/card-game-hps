package com.example.gamehandlerservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    val userHandshakeInterceptor: UserHandshakeInterceptor,
    val accountAndGameHandlerArgumentResolver: AccountAndGameHandlerArgumentResolver
) : WebSocketMessageBrokerConfigurer {
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver?>) {
        argumentResolvers.add(accountAndGameHandlerArgumentResolver)
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/game")
            .addInterceptors(userHandshakeInterceptor)
        registry.addEndpoint("/rooms").setAllowedOrigins("*")
    }
}

