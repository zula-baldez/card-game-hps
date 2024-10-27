package com.example.authservice.controller

import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.example.authservice.service.UserService
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
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

    @PostMapping("/auth/service-token", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @PreAuthorize("hasAuthority('SCOPE_SERVICE') and @AuthUtils.jwtClaimEquals(principal, 'name', #serviceTokenRequest.component2())")
    fun generateTokenForService(@RequestBody @Valid serviceTokenRequest: GenerateServiceTokenRequest): AuthenticationResponse {
        return userService.generateServiceTokenForUser(serviceTokenRequest.userId, serviceTokenRequest.serviceName)
    }

    @GetMapping("/me")
    fun me(auth: Principal): String {
        return auth.name
    }

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadCredentialsException(ex: BadCredentialsException): String {
        return ex.message ?: "Bad credentials"
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): String {
        return ex.message ?: "Failed to authenticate"
    }
}