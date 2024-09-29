package com.example.personalaccount.service

import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository
): AccountService {
    override fun findByIdOrThrow(id: Long): AccountEntity {
        return accountRepository.findByIdOrNull(id) ?: throw IllegalArgumentException("No player found with id $id")
    }
}