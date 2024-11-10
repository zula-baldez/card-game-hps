package com.example.authservice

import com.example.AuthServiceApplication
import com.example.authservice.database.RoleRepository
import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.Mockito
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = [AuthServiceApplication::class])
@ExtendWith(MockitoExtension::class)
@TestPropertySource(properties = ["spring.config.location=classpath:application.yaml"])
class AuthControllerE2ETest : E2EDbInit() {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var personalAccountClient: ReactivePersonalAccountClient

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setup() {
        lenient().`when`(personalAccountClient.createAccount(any()))
            .thenReturn(Mono.empty())
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `register should create a new user and return AuthenticationResponse`() {
        val credentialsRequest = CredentialsRequest(username = "newUser", password = "password123")

        webTestClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(credentialsRequest), CredentialsRequest::class.java)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AuthenticationResponse::class.java)
            .consumeWith { response ->
                val responseBody = response.responseBody
                assert(responseBody?.token?.isNotBlank() == true)
            }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `register should return 400 if username is already taken`() {
        val takenUsername = "existingUser"
        val credentialsRequest = CredentialsRequest(username = takenUsername, password = "password123")

        webTestClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(credentialsRequest), CredentialsRequest::class.java)
            .exchange()
            .expectStatus().isOk

        webTestClient.post()
            .uri("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(credentialsRequest), CredentialsRequest::class.java)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
            .expectBody(String::class.java)
            .consumeWith { response ->
                assert(response.responseBody?.contains("username is taken") == true)
            }
    }
}
