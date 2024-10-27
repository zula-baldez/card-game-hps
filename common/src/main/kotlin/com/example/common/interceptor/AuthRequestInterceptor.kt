package com.example.common.interceptor

import com.example.common.config.RobotCredentialsProperties
import com.example.common.client.AuthServiceClient
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class AuthRequestInterceptor(
    private val robotCredentials: RobotCredentialsProperties,
    private val authServiceClient: AuthServiceClient,
    @Value("\${spring.application.name}")
    private val serviceName: String
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        if (serviceName == "auth-service") {
            return
        }

        val authentication = SecurityContextHolder.getContext().authentication

        val token = if (authentication == null || !authentication.isAuthenticated) {
            authServiceClient.getToken(CredentialsRequest(username = robotCredentials.username!!, password = robotCredentials.password!!)).token
        } else {
            val userId = authentication.name.toLong()
            authServiceClient.getServiceToken(GenerateServiceTokenRequest(userId = userId, serviceName = serviceName)).token
        }

        template.header("Authorization", "Bearer $token")
    }
}
