package com.example.gamehandlerservice.config

import jakarta.inject.Inject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class WebSocketAuthenticationSecurityConfig : WebSocketMessageBrokerConfigurer {
    @Autowired
    private val authChannelInterceptorAdapter: AuthorizationInterceptor? = null

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // Endpoints are already registered on WebSocketConfig, no need to add more.
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(authChannelInterceptorAdapter)
    }
}
