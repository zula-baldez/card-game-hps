package com.example.authservice.config

import com.example.authservice.database.UserRepository
import com.example.authservice.jwt.TokenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class SuccessLoginPasswordHandler(
    val jwtTokenProvider: TokenService, private val userRepo: UserRepository
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
    ) {
        authentication.name?.let { userRepo.findByName(it) }?.let {
            jwtTokenProvider.generateAccessToken(userEntity = it, "auth")
        }?.let {
            response.contentType = "application/json"
            response.writer.write("{ \"token\": \"$it\" }")

        }
    }
}