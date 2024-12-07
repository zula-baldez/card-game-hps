package com.example.authservice.common

import com.example.common.client.ServiceTokenClient
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import com.example.common.interceptor.UserTokenRequestInterceptor
import feign.RequestTemplate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

@ExtendWith(MockitoExtension::class)
class UserTokenRequestInterceptorTest {

    private lateinit var serviceTokenClient: ServiceTokenClient
    private lateinit var serviceName: String
    private lateinit var interceptor: UserTokenRequestInterceptor
    private lateinit var requestTemplate: RequestTemplate
    private lateinit var authentication: Authentication

    @BeforeEach
    fun setUp() {
        serviceTokenClient = mock(ServiceTokenClient::class.java)
        serviceName = "testService"
        interceptor = UserTokenRequestInterceptor(serviceTokenClient, serviceName)
        requestTemplate = RequestTemplate()

        authentication = mock(Authentication::class.java)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    fun `should set Authorization header with Bearer token obtained from service token client`() {
        val userId = 123L
        `when`(authentication.name).thenReturn(userId.toString())
        val tokenResponse = AuthenticationResponse("testToken", 1)
        `when`(serviceTokenClient.getServiceToken(GenerateServiceTokenRequest(userId,"testService"))).thenReturn(tokenResponse)

        interceptor.apply(requestTemplate)

        assert(requestTemplate.headers()["Authorization"]?.first() == "Bearer ${tokenResponse.token}")
    }
}