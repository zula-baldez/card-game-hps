package com.example.authservice.service

import com.example.authservice.database.User
import com.example.authservice.database.UserRepo
import com.example.authservice.dto.RegisterResponse
import com.example.authservice.jwt.TokenService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterService(
    val userRepo: UserRepo,
    val encoder: PasswordEncoder,
    val tokenService: TokenService
) {
    fun register(username: String, password: String) : RegisterResponse {
        val pass = encoder.encode(password)
        val user = User(username, pass)
        userRepo.save(user)
        val token = tokenService.generateAccessToken(user)
        return RegisterResponse(token, user.id)
    }
}