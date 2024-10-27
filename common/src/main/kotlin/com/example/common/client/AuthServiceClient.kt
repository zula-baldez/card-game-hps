package com.example.common.client

import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "auth-service")
interface AuthServiceClient {
    @RequestMapping(method = [RequestMethod.POST], value = ["/auth/login"])
    fun getToken(credentialsRequest: CredentialsRequest): AuthenticationResponse

    @RequestMapping(method = [RequestMethod.POST], value=["/auth/service-token"])
    fun getServiceToken(serviceTokenRequest: GenerateServiceTokenRequest): AuthenticationResponse
}