package com.example.common.interceptor

import com.example.common.client.LoginServiceClient
import com.example.common.client.ServiceTokenClient
import com.example.common.config.RobotCredentialsProperties
import com.example.common.dto.authservice.CredentialsRequest
import feign.RequestInterceptor
import feign.RequestTemplate

class ServiceTokenRequestInterceptor(
    private val loginServiceClient: LoginServiceClient,
    private val robotCredentials: RobotCredentialsProperties
) : RequestInterceptor {
    override fun apply(request: RequestTemplate) {
        val token = loginServiceClient.getToken(CredentialsRequest(robotCredentials.username!!, robotCredentials.password!!)).token
        request.headers()["Authorization"] = listOf("Bearer $token")
    }
}