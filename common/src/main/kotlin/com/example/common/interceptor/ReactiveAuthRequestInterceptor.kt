package com.example.common.interceptor

import com.example.common.client.ReactiveAuthServiceClient
import com.example.common.config.RobotCredentialsProperties
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import reactivefeign.client.ReactiveHttpRequest
import reactivefeign.client.ReactiveHttpRequestInterceptor
import reactor.core.publisher.Mono

@Component
class ReactiveAuthRequestInterceptor(
    private val robotCredentials: RobotCredentialsProperties,
    private val authServiceClient: ReactiveAuthServiceClient,
    @Value("\${spring.application.name}")
    private val serviceName: String
) : ReactiveHttpRequestInterceptor {
    override fun apply(request: ReactiveHttpRequest): Mono<ReactiveHttpRequest> {
        return ReactiveSecurityContextHolder
            .getContext()
            .map { it.authentication.name.toLong() }
            .flatMap { getUserToken(it) }
            .switchIfEmpty(getServiceAccountToken())
            .map { token ->
                request.headers()["Authorization"] = listOf("Bearer $token")
                return@map request
            }
    }

    private fun getServiceAccountToken(): Mono<String> {
        return authServiceClient
            .getToken(CredentialsRequest(username = robotCredentials.username!!, password = robotCredentials.password!!))
            .map { it.token }
    }

    private fun getUserToken(userId: Long): Mono<String> {
        return authServiceClient
            .getServiceToken(GenerateServiceTokenRequest(userId = userId, serviceName = serviceName))
            .map { it.token }
    }
}