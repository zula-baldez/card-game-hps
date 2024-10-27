package com.example.common.interceptor

import com.example.common.client.ReactiveServiceTokenClient
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import reactivefeign.client.ReactiveHttpRequest
import reactivefeign.client.ReactiveHttpRequestInterceptor
import reactor.core.publisher.Mono

class ReactiveUserTokenRequestInterceptor(
    private val serviceTokenClient: ReactiveServiceTokenClient,
    private val serviceName: String
) : ReactiveHttpRequestInterceptor {
    override fun apply(request: ReactiveHttpRequest): Mono<ReactiveHttpRequest> {
        return ReactiveSecurityContextHolder
            .getContext()
            .map { it.authentication.name.toLong() }
            .flatMap { getUserToken(it) }
            .map { token ->
                request.headers()["Authorization"] = listOf("Bearer $token")
                return@map request
            }
    }

    private fun getUserToken(userId: Long): Mono<String> {
        return serviceTokenClient
            .getServiceToken(GenerateServiceTokenRequest(userId = userId, serviceName = serviceName))
            .map { it.token }
    }
}