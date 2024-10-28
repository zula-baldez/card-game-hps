package com.example.common.interceptor

import com.example.common.client.ServiceTokenClient
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.security.core.context.SecurityContextHolder

class UserTokenRequestInterceptor(
    private val serviceTokenClient: ServiceTokenClient,
    private val serviceName: String
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val authentication = SecurityContextHolder.getContext().authentication
        val token = serviceTokenClient.getServiceToken(GenerateServiceTokenRequest(authentication.name.toLong(), serviceName)).token
        template.header("Authorization", "Bearer $token")
    }
}
