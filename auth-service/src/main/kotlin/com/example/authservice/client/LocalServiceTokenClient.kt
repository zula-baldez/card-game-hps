package com.example.authservice.client

import com.example.authservice.service.UserService
import com.example.common.client.ReactiveServiceTokenClient
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
@Primary
class LocalServiceTokenClient(
    private val userService: UserService
) : ReactiveServiceTokenClient {
    override fun getServiceToken(serviceTokenRequest: GenerateServiceTokenRequest): Mono<AuthenticationResponse> {
        return Mono.fromRunnable {
            userService.generateServiceTokenForUser(serviceTokenRequest.userId, serviceTokenRequest.serviceName)
        }
    }
}