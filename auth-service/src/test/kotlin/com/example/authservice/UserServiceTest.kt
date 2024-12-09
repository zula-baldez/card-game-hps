package com.example.authservice

import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepository
import com.example.authservice.jwt.TokenService
import com.example.authservice.service.UserService
import com.example.common.dto.authservice.AuthenticationResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceTest {

    private lateinit var userRepo: UserRepository
    private lateinit var encoder: PasswordEncoder
    private lateinit var tokenService: TokenService
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepo = mock(UserRepository::class.java)
        encoder = mock(PasswordEncoder::class.java)
        tokenService = mock(TokenService::class.java)
        userService = UserService(userRepo, encoder, tokenService)
    }

    @Test
    fun `login should return token for valid credentials`() {
        val username = "testUser"
        val password = "testPassword"
        val user = UserEntity(id = 1L, name = username, password = encoder.encode(password))
        val token = "generatedToken"
        val expectedResponse = AuthenticationResponse(token, user.id!!)

        `when`(userRepo.findByName(username)).thenReturn(user)
        `when`(encoder.matches(password, user.password)).thenReturn(true)
        `when`(tokenService.generateAccessToken(user, "user-token")).thenReturn(token)

        val result = userService.login(username, password)
        assertEquals(expectedResponse, result)
    }


    @Test
    fun `login should throw BadCredentialsException for incorrect password`() {
        val username = "testUser"
        val password = "wrongPassword"
        val user = UserEntity(id = 1L, name = username, password = "encodedPassword")

        `when`(userRepo.findByName(username)).thenReturn(user)
        `when`(encoder.matches(password, user.password)).thenReturn(false)
        val exception = assertThrows<BadCredentialsException> {
            userService.login(username, password)
        }

        assertEquals("Incorrect password", exception.message)
    }

    @Test
    fun `generateServiceTokenForUser should return token for existing user`() {
        val userId = 1L
        val service = "service-name"
        val user = UserEntity(id = userId, name = "testUser", password = "encodedPassword")
        val token = "generatedServiceToken"
        val expectedResponse = AuthenticationResponse(token, user.id!!)

        `when`(userRepo.findById(userId)).thenReturn(Optional.of(user))
        `when`(tokenService.generateAccessToken(user, service)).thenReturn(token)

        val result = userService.generateServiceTokenForUser(userId, service)

        assertEquals(expectedResponse, result)

    }

}