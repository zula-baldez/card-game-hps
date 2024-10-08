package com.example.personalaccount.service

import com.example.authservice.database.UserEntity
import com.example.personalaccount.database.AccountEntity

interface AccountService {
    fun findByIdOrThrow(id: Long): AccountEntity
    fun createAccountForUser(userEntity: UserEntity): AccountEntity
}