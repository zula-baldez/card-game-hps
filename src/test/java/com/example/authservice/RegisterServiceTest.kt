package com.example.authservice

import com.example.authservice.database.User
import com.example.authservice.database.UserRepo
import com.example.authservice.jwt.TokenService
import com.example.authservice.service.RegisterService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.security.crypto.password.PasswordEncoder

class RegisterServiceTest {

    @Mock
    private lateinit var userRepo: UserRepo

    @Mock
    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var encoder: PasswordEncoder

    @InjectMocks
    private lateinit var registerService: RegisterService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should register user and return RegisterResponse`() {
        val username = "testUser"
        val password = "testPassword"
        val encodedPassword = "encodedPassword"
        val userId = 1L
        val user = User(username, encodedPassword).apply { id = userId }
        val expectedToken = "generatedToken"

        `when`(encoder.encode(password)).thenReturn(encodedPassword)
        `when`(tokenService.generateAccessToken(user)).thenReturn(expectedToken)
        `when`(userRepo.save(any(User::class.java))).thenReturn(user)

        val response = registerService.register(username, password)
        assertEquals(expectedToken, response.token)
        assertEquals(userId, response.id)
        verify(encoder).encode(password)
        verify(userRepo).save(any(User::class.java))
        verify(tokenService).generateAccessToken(user)
    }
}