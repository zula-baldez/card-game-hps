package com.example.authservice

import com.example.authservice.database.RoleRepository
import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepository
import com.example.authservice.jwt.TokenService
import com.example.authservice.service.RegistrationService
import com.example.common.client.ReactivePersonalAccountClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import reactor.test.StepVerifier

class RegistrationServiceTest {

    private lateinit var registrationService: RegistrationService
    private lateinit var userRepo: UserRepository
    private lateinit var roleRepository: RoleRepository
    private lateinit var encoder: PasswordEncoder
    private lateinit var tokenService: TokenService
    private lateinit var personalAccountClient: ReactivePersonalAccountClient
    private lateinit var jwtDecoder: JwtDecoder

    @BeforeEach
    fun setUp() {
        userRepo = mock(UserRepository::class.java)
        roleRepository = mock(RoleRepository::class.java)
        encoder = mock(PasswordEncoder::class.java)
        tokenService = mock(TokenService::class.java)
        personalAccountClient = mock(ReactivePersonalAccountClient::class.java)
        jwtDecoder = mock(JwtDecoder::class.java)

        registrationService = RegistrationService(
            userRepo,
            roleRepository,
            encoder,
            tokenService,
            personalAccountClient,
            jwtDecoder
        )
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