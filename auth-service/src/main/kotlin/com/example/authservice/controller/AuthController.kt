package com.example.authservice.controller

import com.example.common.client.AuthenticationResponse
import com.example.common.client.CredentialsRequest
import com.example.authservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class AuthController(
    val userService: UserService,
) {
    @PostMapping("/auth/register", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody @Valid credentialsRequest: CredentialsRequest): AuthenticationResponse {
        return userService.register(credentialsRequest.username, credentialsRequest.password)
    }

    @PostMapping("/auth/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@RequestBody @Valid credentialsRequest: CredentialsRequest): AuthenticationResponse {
        return userService.login(credentialsRequest.username, credentialsRequest.password)
    }

    @GetMapping("/me")
    fun me(auth: Principal): String {
        return auth.name
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): String {
        return ex.message ?: "Failed to authenticate"
    }
}