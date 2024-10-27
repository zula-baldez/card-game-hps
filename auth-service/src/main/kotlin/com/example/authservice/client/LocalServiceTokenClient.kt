package com.example.authservice.client

import com.example.authservice.service.UserService
import com.example.common.client.ServiceTokenClient
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class LocalServiceTokenClient(
    private val userService: UserService
) : ServiceTokenClient {
    override fun getServiceToken(serviceTokenRequest: GenerateServiceTokenRequest): AuthenticationResponse {
        return userService.generateServiceTokenForUser(serviceTokenRequest.userId, serviceTokenRequest.serviceName)
    }
}