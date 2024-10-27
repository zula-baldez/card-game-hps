package com.example.common.interceptor

import com.example.common.RobotCredentials
import com.example.common.client.AuthServiceClient
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder

class AuthRequestInterceptor(
    private val robotCredentials: RobotCredentials,
    private val authServiceClient: AuthServiceClient,
    @Value("\${spring.application.name}")
    private val serviceName: String
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val token = if (SecurityContextHolder.getContext().authentication.isAuthenticated) {
            authServiceClient.getToken(CredentialsRequest(username = robotCredentials.username, password = robotCredentials.password)).token
        } else {
            val userId = SecurityContextHolder.getContext().authentication.name.toLong()
            authServiceClient.getServiceToken(GenerateServiceTokenRequest(userId = userId, serviceName = serviceName)).token
        }
        template.header("Authorization", "Bearer $token")
    }
}
