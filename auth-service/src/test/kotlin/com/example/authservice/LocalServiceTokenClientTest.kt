package com.example.authservice

import com.example.authservice.client.LocalServiceTokenClient
import com.example.authservice.service.RegistrationService
import com.example.authservice.service.UserService
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono

class LocalServiceTokenClientTest {

    private lateinit var localServiceTokenClient: LocalServiceTokenClient

    @Mock
    private lateinit var userService: UserService
    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        localServiceTokenClient = LocalServiceTokenClient(userService)
    }
    @Test
    fun `should return service token response when called`() {
        val userId = 1L
        val serviceName = "TestService"
        val serviceTokenRequest = GenerateServiceTokenRequest(userId, serviceName)
        val expectedResponse = AuthenticationResponse("token123", userId)

        whenever(userService.generateServiceTokenForUser(userId, serviceName)).thenReturn(Mono.just(expectedResponse))

        val result = localServiceTokenClient.getServiceToken(serviceTokenRequest).block()

        assertEquals(expectedResponse, result)
        verify(userService).generateServiceTokenForUser(userId, serviceName)
    }
}