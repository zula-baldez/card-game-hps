package com.example.authservice.service

import com.example.authservice.database.RoleRepository
import com.example.authservice.database.UserEntity
import com.example.authservice.database.UserRepository
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.util.Role
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class RegistrationService(
    private val userRepo: UserRepository,
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder,
    private val createAccountDtoSender: CreateAccountDtoSender,
) {
    @Transactional
    fun register(username: String, password: String) {
        if (userRepo.findByName(username) != null) {
            throw BadCredentialsException("username is taken")
        }

        val pass = encoder.encode(password)
        val userEntity = UserEntity(username, pass)

        val userRole = roleRepository.findFirstByRoleName(Role.USER)
        userEntity.roles += userRole
        val savedUser = userRepo.save(userEntity)

        createAccountDtoSender.sendCreateAccount(CreateAccountDto(savedUser.id!!, username))
    }

    @Transactional
    fun commitRegistration(userId: Long) {
        val user = userRepo.findById(userId).getOrNull() ?: throw IllegalArgumentException("user not found")
        user.registered = true
        userRepo.save(user)
    }

    @Transactional
    fun rollbackRegistration(userId: Long) {
        userRepo.deleteById(userId)
    }
}