package com.example.authservice.config

import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepo
import com.example.authservice.jwt.TokenService
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class SuccessOAuth2Handler(val jwtTokenProvider: TokenService, private val userRepo: UserRepo) :
    AuthenticationSuccessHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        if (authentication is OAuth2AuthenticationToken) {
            val name = authentication.principal.name
            val userEntity: UserEntity = userRepo.findByName(name) ?: run {
                val newUserEntity = UserEntity(name = name, password = "vrebegdvsrtbvetbe") //todo generate password
                userRepo.save(newUserEntity)
                newUserEntity
            }
            // Generate the JWT token
            val jwtToken = jwtTokenProvider.generateAccessToken(userEntity)
            response.contentType = "application/json"
            response.writer.write("{ \"token\": \"$jwtToken\" }")
        }
    }
}