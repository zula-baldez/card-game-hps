package com.example.common.interceptor

import com.example.common.RobotCredentials
import com.example.common.client.AuthServiceClient
import com.example.common.dto.authservice.CredentialsRequest
import feign.RequestInterceptor
import feign.RequestTemplate

class AuthRequestInterceptor(
    private val robotCredentials: RobotCredentials,
    private val authServiceClient: AuthServiceClient
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val token = authServiceClient.getToken(CredentialsRequest(robotCredentials.username, robotCredentials.password))
        template.header("Authorization", "Bearer ${token.token}")
    }
}
