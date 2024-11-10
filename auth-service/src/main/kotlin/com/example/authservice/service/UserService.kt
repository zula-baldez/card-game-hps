package com.example.authservice.service

import com.example.authservice.database.UserRepository
import com.example.authservice.jwt.TokenService
import com.example.common.dto.authservice.AuthenticationResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    val userRepo: UserRepository,
    val encoder: PasswordEncoder,
    val tokenService: TokenService
) {
    fun login(username: String, password: String): Mono<AuthenticationResponse> {
        val user = userRepo.findByName(username) ?: throw UsernameNotFoundException("not found")

        return if (encoder.matches(password, user.password)) {
            val token = tokenService.generateAccessToken(user, "user-token")
            Mono.just(AuthenticationResponse(token, user.id!!))
        } else Mono.error(BadCredentialsException("Incorrect password"))
    }

    fun generateServiceTokenForUser(userId: Long, service: String): Mono<AuthenticationResponse> {
        val user = userRepo.findById(userId).getOrNull() ?: throw UsernameNotFoundException("user with id $userId not found")
        val token = tokenService.generateAccessToken(user, service)

        return Mono.just(AuthenticationResponse(
            token,
            user.id!!
        ))
    }
}