package com.example.authservice
import com.example.authservice.controller.AuthController
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.authservice.service.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.security.Principal

class AuthControllerTest {

    private val registerService: UserService = mock(UserService::class.java)
    private val authController = AuthController(registerService)

    @Test
    fun `should register user and return RegisterResponse`() {
        val credentialsRequest = CredentialsRequest("testUser", "testPass")
        val token = "tokenString"
        val userId = 1L
        val authenticationResponse = AuthenticationResponse(token, userId)

        `when`(registerService.register(credentialsRequest.username, credentialsRequest.password))
            .thenReturn(authenticationResponse)

        val response = authController.register(credentialsRequest)

        assertEquals(token, response.token)
        assertEquals(userId, response.id)
    }

    @Test
    fun `should return username from Principal`() {
        val principal = mock(Principal::class.java)
        `when`(principal.name).thenReturn("testUser")
        val result = authController.me(principal)
        assertEquals("testUser", result)
    }
}