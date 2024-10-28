package com.example.authservice

import com.example.authservice.database.RoleEntity
import com.example.authservice.database.RoleRepository
import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepository
import com.example.authservice.jwt.TokenService
import org.mockito.kotlin.any
import com.example.authservice.service.RegistrationService
import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.util.Role
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

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
    private lateinit var jwtDecoder: JwtDecoder

    private lateinit var registrationService: RegistrationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        registrationService = RegistrationService(userRepo, roleRepository, encoder, tokenService, personalAccountClient, jwtDecoder)
    }
    @Test
    fun `register should throw BadCredentialsException when username is already taken`() {
        val username = "existingUser"
        val password = "testPassword"

        `when`(userRepo.findByName(username)).thenReturn(UserEntity(username, "somePassword"))

        val result = registrationService.register(username, password)

        StepVerifier.create(result)
            .expectError(BadCredentialsException::class.java)
            .verify()

        verify(userRepo).findByName(username)
        verifyNoMoreInteractions(userRepo)
    }
}