package com.example.personalaccount.service

import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.personalaccount.database.AccountEntity

interface AccountService {
    fun findByIdOrThrow(id: Long): AccountEntity
    fun createAccountForUser(createAccountDto: CreateAccountDto): AccountEntity
}