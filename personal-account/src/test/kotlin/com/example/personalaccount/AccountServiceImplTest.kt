package com.example.personalaccount

import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.service.AccountServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class AccountServiceImplTest {

    private val accountRepository: AccountRepository = Mockito.mock(AccountRepository::class.java)
    private val accountService = AccountServiceImpl(accountRepository)

    @Test
    fun `should return account when found by id`() {
        val accountId = 1L
        val expectedAccount = AccountEntity(id = 1, name = "Alice", fines = 0, mutableSetOf(), mutableSetOf(), 1)

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount))
        val result = accountService.findByIdOrThrow(accountId)

        assertEquals(expectedAccount, result)
    }

    @Test
    fun `should return existing account when available`() {
        val userEntity = CreateAccountDto(
            1,
            "testUser"
        )
        val existingAccount = AccountEntity(id = 1, name = "Alice", fines = 0, mutableSetOf(), mutableSetOf(), 1)

        whenever(accountRepository.findById(1)) doReturn Optional.of(existingAccount)
        val result = accountService.createAccountForUser(userEntity)
        assertEquals(result, existingAccount)
        verify(accountRepository, never()).save(any())
    }

}



