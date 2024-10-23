package com.example.common.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient
interface AuthServiceClient {
    @RequestMapping(method = [RequestMethod.POST], value = ["/auth/login"])
    fun getToken(credentialsRequest: CredentialsRequest): AuthenticationResponse;
}