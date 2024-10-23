package com.example.authservice.service

import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepo
import com.example.authservice.dto.AuthenticationResponse
import com.example.authservice.jwt.TokenService
//import com.example.personalaccount.service.AccountService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepo: UserRepo,
    val encoder: PasswordEncoder,
    val tokenService: TokenService
) {
    fun register(username: String, password: String) : AuthenticationResponse {
        val pass = encoder.encode(password)
        val userEntity = UserEntity(username, pass)
        val savedUser = userRepo.save(userEntity)
        val token = tokenService.generateAccessToken(savedUser)
        return AuthenticationResponse(token, savedUser.id!!)
    }

    fun login(username: String, password: String): AuthenticationResponse {
        val pass = encoder.encode(password)
        val user = userRepo.findByName(username) ?: throw UsernameNotFoundException("not found")

        if (user.password == pass) {
            val token = tokenService.generateAccessToken(user)
            return AuthenticationResponse(token, user.id!!)
        } else throw BadCredentialsException("")
    }
}