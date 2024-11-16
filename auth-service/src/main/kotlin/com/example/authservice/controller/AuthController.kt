package com.example.authservice.controller

import com.example.authservice.service.RegistrationService
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.example.authservice.service.UserService
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@Tag(name = "auth_controller", description = "Rest API for authentication")
class AuthController(
    val userService: UserService,
    val registrationService: RegistrationService
) {
    @PostMapping("/auth/register")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    fun register(@RequestBody @Valid credentialsRequest: CredentialsRequest): Mono<AuthenticationResponse> {
        return registrationService.register(credentialsRequest.username, credentialsRequest.password)
    }

    @PostMapping("/auth/login")
    fun login(@RequestBody @Valid credentialsRequest: CredentialsRequest): Mono<AuthenticationResponse> {
        return userService.login(credentialsRequest.username, credentialsRequest.password)
    }

    @PostMapping("/auth/service-token")
    @PreAuthorize("hasAuthority('SCOPE_SERVICE') and @AuthUtils.jwtClaimEquals(principal, 'name', #serviceTokenRequest.component2())")
    fun generateTokenForService(@RequestBody @Valid serviceTokenRequest: GenerateServiceTokenRequest): Mono<AuthenticationResponse> {
        return userService.generateServiceTokenForUser(serviceTokenRequest.userId, serviceTokenRequest.serviceName)
    }

    @GetMapping("/me")
    fun me(auth: Principal): Mono<String> {
        return Mono.just(auth.name)
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