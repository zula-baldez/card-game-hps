package com.example.common.service

import com.example.common.database.UserEntity
import com.example.common.database.UserRepo
import com.example.personalaccount.service.AccountService
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepo: UserRepo,
    val accountService: AccountService,
) {
    fun createUser(username: String) : Long {
        val userEntity = UserEntity(username, "password")
        val savedUser = userRepo.save(userEntity)
        accountService.createAccountForUser(savedUser)
        return savedUser.id!!
    }
}