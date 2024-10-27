package com.example.common.interceptor

import com.example.common.RobotCredentials
import com.example.common.client.AuthServiceClient
import com.example.common.dto.authservice.CredentialsRequest
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.web.context.request.RequestContextHolder

class AuthRequestInterceptor(
    private val robotCredentials: RobotCredentials,
    private val authServiceClient: AuthServiceClient
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        RequestContextHolder.getRequestAttributes()
        val token = authServiceClient.getToken(CredentialsRequest(robotCredentials.username, robotCredentials.password))
        template.header("Authorization", "Bearer ${token.token}")
    }
}
