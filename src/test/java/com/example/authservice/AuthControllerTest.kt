package com.example.authservice
import com.example.authservice.auth.AuthController
import com.example.authservice.dto.RegisterRequest
import com.example.authservice.dto.RegisterResponse
import com.example.authservice.service.RegisterService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.security.Principal

class AuthControllerTest {

    private val registerService: RegisterService = mock(RegisterService::class.java)
    private val authController = AuthController(registerService)

    @Test
    fun `should register user and return RegisterResponse`() {
        val registerRequest = RegisterRequest("testUser", "testPass")
        val token = "tokenString"
        val userId = 1L
        val registerResponse = RegisterResponse(token, userId)

        `when`(registerService.register(registerRequest.username, registerRequest.password))
            .thenReturn(registerResponse)

        val response = authController.register(registerRequest)

        assertEquals(token, response.token)
        assertEquals(userId, response.id)
    }

    @Test
    fun `should return username from Principal`() {
        val principal = mock(Principal::class.java)
        `when`(principal.name).thenReturn("testUser")
        val result = authController.register(principal)
        assertEquals("testUser", result)
    }
}