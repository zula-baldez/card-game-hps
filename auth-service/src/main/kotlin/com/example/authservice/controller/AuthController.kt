package com.example.authservice.controller

import com.example.authservice.service.RegistrationService
import com.example.authservice.service.UserService
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.authservice.CredentialsRequest
import com.example.common.dto.authservice.GenerateServiceTokenRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "auth_controller", description = "Rest API for authentication")
class AuthController(
    val userService: UserService,
    val registrationService: RegistrationService
) {
    @PostMapping("/auth/register")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Operation(summary = "Register user")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody @Valid credentialsRequest: CredentialsRequest): Mono<Void> {
        return Mono.fromRunnable {
            registrationService.register(credentialsRequest.username, credentialsRequest.password)
        }
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Login")
    fun login(@RequestBody @Valid credentialsRequest: CredentialsRequest): Mono<AuthenticationResponse> {
        return Mono.fromCallable {
            userService.login(credentialsRequest.username, credentialsRequest.password)
        }
    }

    @PostMapping("/auth/service-token")
    @PreAuthorize("hasAuthority('SCOPE_SERVICE') and @AuthUtils.jwtClaimEquals(principal, 'name', #serviceTokenRequest.component2())")
    @Operation(summary = "Generate token for service")
    fun generateTokenForService(@RequestBody @Valid serviceTokenRequest: GenerateServiceTokenRequest): Mono<AuthenticationResponse> {
        return Mono.fromCallable {
            userService.generateServiceTokenForUser(serviceTokenRequest.userId, serviceTokenRequest.serviceName)
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Return auth name")
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