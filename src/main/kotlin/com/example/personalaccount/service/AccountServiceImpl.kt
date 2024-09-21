package com.example.personalaccount.service

import com.example.personalaccount.database.Account
import com.example.personalaccount.database.AccountRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
    private val accountRepo: AccountRepo
): AccountService {
    override fun findByIdOrThrow(id: Long): Account {
        return accountRepo.findByIdOrNull(id) ?: throw IllegalArgumentException("No player found with id $id")
    }
}