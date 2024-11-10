package com.example.authservice.common

import com.example.common.client.LoginServiceClient
import com.example.common.config.RobotCredentialsProperties
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.interceptor.ServiceTokenRequestInterceptor
import feign.RequestTemplate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockitoExtension::class)
class ServiceTokenRequestInterceptorTest {

    private lateinit var loginServiceClient: LoginServiceClient
    private lateinit var robotCredentials: RobotCredentialsProperties
    private lateinit var interceptor: ServiceTokenRequestInterceptor
    private lateinit var requestTemplate: RequestTemplate

    @BeforeEach
    fun setUp() {
        loginServiceClient = mock(LoginServiceClient::class.java)
        robotCredentials = RobotCredentialsProperties("testUsername", "testPassword")
        interceptor = ServiceTokenRequestInterceptor(loginServiceClient, robotCredentials)
        requestTemplate = RequestTemplate()
    }

    @Test
    fun `should set Authorization header with Bearer token from login service`() {
        val tokenResponse = AuthenticationResponse("testToken", 1)
        `when`(loginServiceClient.getToken(CredentialsRequest("testUsername", "testPassword"))).thenReturn(tokenResponse)

        interceptor.apply(requestTemplate)

        assertEquals(requestTemplate.headers()["Authorization"]?.first(), "Bearer ${tokenResponse.token}")
    }
}