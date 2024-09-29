package com.example.authservice.auth

import com.example.authservice.dto.CredentialsRequest
import com.example.authservice.dto.AuthenticationResponse
import com.example.authservice.service.UserService
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class AuthController(
    val userService: UserService,
) {
    @PostMapping("/auth/register", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody credentialsRequest: CredentialsRequest): AuthenticationResponse {
        return userService.register(credentialsRequest.username, credentialsRequest.password)
    }

    @PostMapping("/auth/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@RequestBody credentialsRequest: CredentialsRequest): AuthenticationResponse {
        return userService.login(credentialsRequest.username, credentialsRequest.password)
    }

    @GetMapping("/me")
    fun me(auth: Principal): String {
        return auth.name
    }
}