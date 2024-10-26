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

    override fun createAccountForUser(id: Long, name: String): AccountEntity {
        var account = accountRepository.findByIdOrNull(id)

        if (account == null) {
            account = AccountEntity(
                id = id,
                name = name,
                fines = 0
            )

            accountRepository.save(account)
        }

        return account
    }
}