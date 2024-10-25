package com.example.common.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "auth-client", url = "http://localhost:8081")
interface AuthServiceClient {
    @RequestMapping(method = [RequestMethod.POST], value = ["/auth/login"])
    fun getToken(credentialsRequest: CredentialsRequest): AuthenticationResponse;
}