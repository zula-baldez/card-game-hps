package com.example.personalaccount.service

import com.example.personalaccount.database.AccountEntity

interface AccountService {
    fun findByIdOrThrow(id: Long): AccountEntity
    fun createAccountForUser(id: Long, name: String): AccountEntity
}