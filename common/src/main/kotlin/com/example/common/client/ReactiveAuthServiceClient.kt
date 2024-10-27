package com.example.common.client

import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient("auth-service")
interface ReactiveAuthServiceClient {
    @RequestMapping(method = [RequestMethod.POST], value = ["/auth/login"])
    fun getToken(credentialsRequest: CredentialsRequest): Mono<AuthenticationResponse>

    @RequestMapping(method = [RequestMethod.POST], value=["/auth/service-token"])
    fun getServiceToken(serviceTokenRequest: GenerateServiceTokenRequest): Mono<AuthenticationResponse>
}