package com.example.personalaccount.service

import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository
): AccountService {
    override fun findByIdOrThrow(id: Long): AccountEntity {
        return accountRepository.findByIdOrNull(id) ?: throw AccountNotFoundException(id)
    }

    override fun createAccountForUser(username: String): AccountEntity {
        val account = AccountEntity(
            name = username,
            fines = 0
        )

        accountRepository.save(account)

        return account
    }
}