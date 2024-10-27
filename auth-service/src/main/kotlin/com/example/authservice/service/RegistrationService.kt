package com.example.authservice.service

import com.example.authservice.database.RoleRepository
import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepository
import com.example.authservice.jwt.TokenService
import com.example.common.client.PersonalAccountClient
import com.example.common.dto.authservice.AuthenticationResponse
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.util.Role
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegistrationService(
    private val userRepo: UserRepository,
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val personalAccountClient: PersonalAccountClient
) {
    @Transactional
    fun register(username: String, password: String) : AuthenticationResponse {
        if (userRepo.findByName(username) != null) {
            throw BadCredentialsException("username is taken")
        }

        val pass = encoder.encode(password)
        val userEntity = UserEntity(username, pass)
        val userRole = roleRepository.findFirstByRoleName(Role.USER)
        userEntity.roles += userRole
        val savedUser = userRepo.save(userEntity)
        val token = tokenService.generateAccessToken(savedUser, "user-token")
        personalAccountClient.createAccount(CreateAccountDto(id = savedUser.id!!, username = username),)
        return AuthenticationResponse(token, savedUser.id!!)
    }
}