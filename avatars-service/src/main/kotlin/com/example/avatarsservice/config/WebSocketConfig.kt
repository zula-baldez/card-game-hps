package com.example.avatarsservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.converter.DefaultContentTypeResolver
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.util.MimeTypeUtils
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean


@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val objectMapper: ObjectMapper
) : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/app/avatars")
            .setAllowedOrigins("*")
    }

    override fun configureMessageConverters(messageConverters: MutableList<MessageConverter?>): Boolean {
        val resolver = DefaultContentTypeResolver()
        resolver.defaultMimeType = MimeTypeUtils.APPLICATION_JSON
        val converter = MappingJackson2MessageConverter()
        converter.objectMapper = objectMapper
        converter.contentTypeResolver = resolver
        messageConverters.add(converter)
        return false
    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        registry.setMessageSizeLimit(4 * 1024 * 1024)
        registry.setSendBufferSizeLimit(4 * 1024 * 1024)
        registry.setSendTimeLimit(4 * 1024 * 1024);
    }

    @Bean
    fun createServletServerContainerFactoryBean(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean()
        container.setMaxTextMessageBufferSize(4 * 1024 * 1024)
        container.setMaxSessionIdleTimeout(4 * 1024 * 1024)
        container.setAsyncSendTimeout(4 * 1024 * 1024)
        container.setMaxBinaryMessageBufferSize(4 * 1024 * 1024)
        return container
    }
}

