package com.example.authservice.service

import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepo
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.authservice.jwt.TokenService
import com.example.common.client.PersonalAccountClient
import com.example.common.dto.personalaccout.CreateAccountDto
//import com.example.personalaccount.service.AccountService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userRepo: UserRepo,
    val encoder: PasswordEncoder,
    val tokenService: TokenService,
    val personalAccountClient: PersonalAccountClient
) {
    @Transactional
    fun register(username: String, password: String) : AuthenticationResponse {
        if (userRepo.findByName(username) != null) {
            throw BadCredentialsException("username is taken")
        }

        val pass = encoder.encode(password)
        val userEntity = UserEntity(username, pass)
        val savedUser = userRepo.save(userEntity)
        personalAccountClient.createAccount(CreateAccountDto(savedUser.id!!, savedUser.name!!))
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