package com.example.common.interceptor

import com.example.common.client.ReactiveLoginServiceClient
import com.example.common.config.RobotCredentialsProperties
import com.example.common.dto.authservice.CredentialsRequest
import reactivefeign.client.ReactiveHttpRequest
import reactivefeign.client.ReactiveHttpRequestInterceptor
import reactor.core.publisher.Mono

class ReactiveServiceTokenRequestInterceptor(
    private val loginServiceClient: ReactiveLoginServiceClient,
    private val robotCredentials: RobotCredentialsProperties
): ReactiveHttpRequestInterceptor {
    override fun apply(request: ReactiveHttpRequest): Mono<ReactiveHttpRequest> {
        return getServiceAccountToken()
            .map { token ->
                request.headers()["Authorization"] = listOf("Bearer $token")
                return@map request
            }
    }

    private fun getServiceAccountToken(): Mono<String> {
        return loginServiceClient
            .getToken(CredentialsRequest(username = robotCredentials.username!!, password = robotCredentials.password!!))
            .map { it.token }
    }
}