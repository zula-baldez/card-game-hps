package com.example.common.client

import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "auth-service", contextId = "login-service", primary = false)
interface LoginServiceClient {
    @RequestMapping(method = [RequestMethod.POST], value = ["/auth/login"])
    fun getToken(credentialsRequest: CredentialsRequest): AuthenticationResponse
}