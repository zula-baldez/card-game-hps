package com.example.authservice

import com.example.authservice.controller.AuthController
import com.example.authservice.service.RegistrationService
import com.example.authservice.service.UserService
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.BadCredentialsException
import reactor.test.StepVerifier
import java.security.Principal

class AuthControllerTest {

    private val userService: UserService = mock()
    private val registrationService: RegistrationService = mock()
    private val authController = AuthController(userService, registrationService)


    @Test
    fun `login should return AuthenticationResponse when login is successful`() {
        val credentialsRequest = CredentialsRequest("username", "password")
        val authResponse = AuthenticationResponse("token", 1)

        whenever(userService.login(credentialsRequest.username, credentialsRequest.password)).thenReturn(
            authResponse
        )

        StepVerifier.create(authController.login(credentialsRequest))
            .expectNext(authResponse)
            .verifyComplete()
    }

    @Test
    fun `generateTokenForService should return AuthenticationResponse when token generation is successful`() {
        val serviceTokenRequest = GenerateServiceTokenRequest(1, "serviceName")
        val authResponse = AuthenticationResponse("token", 1)

        whenever(
            userService.generateServiceTokenForUser(
                serviceTokenRequest.userId,
                serviceTokenRequest.serviceName
            )
        ).thenReturn(authResponse)

        StepVerifier.create(authController.generateTokenForService(serviceTokenRequest))
            .expectNext(authResponse)
            .verifyComplete()
    }

    @Test
    fun `me should return the name of the authenticated user`() {
        val principal: Principal = mock { on { name } doReturn "username" }

        StepVerifier.create(authController.me(principal))
            .expectNext("username")
            .verifyComplete()
    }

    @Test
    fun `handleBadCredentialsException should return unauthorized response`() {
        val exception = BadCredentialsException("Invalid credentials")
        val response = authController.handleBadCredentialsException(exception)

        assertEquals("Invalid credentials", response)
    }

    @Test
    fun `handleIllegalArgumentException should return bad request response`() {
        val exception = IllegalArgumentException("Failed to authenticate")

        val response = authController.handleIllegalArgumentException(exception)

        assertEquals("Failed to authenticate", response)
    }
}