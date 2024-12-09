package com.example.authservice

import com.example.authservice.database.RoleEntity
import com.example.authservice.database.RoleRepository
import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepository
import com.example.authservice.jwt.TokenService
import com.example.authservice.service.CreateAccountDtoSender
import com.example.authservice.service.RegistrationService
import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.util.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import reactor.core.publisher.Mono

class RegistrationServiceTest {

    @Mock
    private lateinit var userRepo: UserRepository

    @Mock
    private lateinit var roleRepository: RoleRepository

    @Mock
    private lateinit var encoder: PasswordEncoder

    @Mock
    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var personalAccountClient: ReactivePersonalAccountClient

    @Mock
    private lateinit var createAccountDtoSender: CreateAccountDtoSender

    @Mock
    private lateinit var jwtDecoder: JwtDecoder

    private lateinit var registrationService: RegistrationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        registrationService = RegistrationService(userRepo, roleRepository, encoder, createAccountDtoSender)
    }

    @Test
    fun `register should throw BadCredentialsException when username is already taken`() {
        val username = "existingUser"
        val password = "testPassword"

        `when`(userRepo.findByName(username)).thenReturn(UserEntity(username, "somePassword"))
        val exception = assertThrows<BadCredentialsException> {
            registrationService.register(username, password)
        }

        verify(userRepo).findByName(username)
        assertEquals("username is taken", exception.message)
    }

    @Test
    fun `register should create a new user, generate token, and return AuthenticationResponse`() {
        val username = "newUser"
        val password = "password123"
        val encodedPassword = "encodedPassword123"
        val generatedToken = "jwtToken"
        val userId = 1L

        val userRole = RoleEntity(id = 1, roleName = Role.USER)
        val userEntity =
            UserEntity(id = userId, name = username, password = encodedPassword, roles = mutableSetOf(userRole))

        `when`(userRepo.findByName(username)).thenReturn(null)
        `when`(encoder.encode(password)).thenReturn(encodedPassword)
        `when`(roleRepository.findFirstByRoleName(Role.USER)).thenReturn(userRole)
        `when`(userRepo.save(any<UserEntity>())).thenReturn(userEntity)
        `when`(tokenService.generateAccessToken(any(), any())).thenReturn(generatedToken)
        `when`(jwtDecoder.decode(generatedToken)).thenReturn(mock(Jwt::class.java)) // Decoding token returns a mock Jwt
        `when`(personalAccountClient.createAccount(any())).thenReturn(Mono.empty()) // Account creation returns empty

        val result = registrationService.register(username, password)

        verify(userRepo).findByName(username)
        verify(encoder).encode(password)
        verify(roleRepository).findFirstByRoleName(Role.USER)
    }

}