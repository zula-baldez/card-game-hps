package com.example.personalaccount
import com.example.authservice.database.RoleEntity
import com.example.authservice.database.UserEntity
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
        val expectedAccount = AccountEntity(
            name = "User1",
            fines = 0,
            id = accountId
        )

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount))
        val result = accountService.findByIdOrThrow(accountId)

        assertEquals(expectedAccount, result)
    }

    @Test
    fun `should create account when doesnt exist`() {
        val userEntity = UserEntity(
            "testUser",
            "testPassword",
            1
        )

        whenever(accountRepository.findById(1)) doReturn Optional.empty()
        val accountEntity = accountService.createAccountForUser(userEntity)
        verify(accountRepository).save(accountEntity)
        assertEquals(1, accountEntity.id)
        assertEquals("testUser", accountEntity.name)
        assertEquals(0, accountEntity.fines)
    }

    @Test
    fun `should return existing account when available`() {
        val userEntity = UserEntity(
            "testUser",
            "testPassword",
            1
        )
        val existingAccount = AccountEntity(
            1,
            "testUser",
            123
        )

        whenever(accountRepository.findById(1)) doReturn Optional.of(existingAccount)
        val result = accountService.createAccountForUser(userEntity)
        assertEquals(result, existingAccount)
        verify(accountRepository, never()).save(any())
    }

}



