package com.example.authservice

import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest : E2EDbInit() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `test registration`() {
        val credentialsRequest = CredentialsRequest(username = "name_" + LocalDateTime.now(), password = "password")

        val result = mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(credentialsRequest)
        }

        result.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andDo {
            println("Registration successful.")
        }
    }

    @Test
    fun `test login`() {
        val credentialsRegisterRequest =
            CredentialsRequest(username = "name_" + LocalDateTime.now(), password = "password")

        val resultRegister = mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(credentialsRegisterRequest)
        }

        val registerResponse =
            objectMapper.readValue(resultRegister.andReturn().response.contentAsString, ResponseData::class.java)
        val credentialsRequest = CredentialsRequest(
            username = credentialsRegisterRequest.username,
            password = credentialsRegisterRequest.password
        )

        val result = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(credentialsRequest)
        }

        result.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andDo {
            println("Login successful.")
        }

        val response = result.andReturn().response.contentAsString
        val authResponse = objectMapper.readValue(response, AuthenticationResponse::class.java)

            assertEquals(registerResponse.token, authResponse.token)
    }

    @Test
    fun `test get current user`() {
        val credentialsRegisterRequest =
            CredentialsRequest(username = "name_" + LocalDateTime.now(), password = "password")

        val resultRegister = mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(credentialsRegisterRequest)
        }

        val registerResponse =
            objectMapper.readValue(resultRegister.andReturn().response.contentAsString, ResponseData::class.java)

        val credentialsRequest = CredentialsRequest(
            username = credentialsRegisterRequest.username,
            password = credentialsRegisterRequest.password
        )
        val loginResult = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(credentialsRequest)
        }
        val response = loginResult.andReturn().response.contentAsString
        val authResponse = objectMapper.readValue(response, AuthenticationResponse::class.java)

        val userInfoResponse = mockMvc.get("/me") {
            header("Authorization", "Bearer ${authResponse.token}")
        }

        userInfoResponse.andExpect {
            status { isOk() }
            content { string(registerResponse.id.toString()) }
        }.andDo {
            println("User info fetched successfully.")
        }
    }

    data class ResponseData(val token: String, val id: Int)
}