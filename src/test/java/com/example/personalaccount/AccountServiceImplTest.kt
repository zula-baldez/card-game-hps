package com.example.personalaccount
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.service.AccountServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.data.repository.findByIdOrNull
import java.util.Optional

class AccountServiceImplTest {

    private val accountRepository: AccountRepository = Mockito.mock(AccountRepository::class.java)
    private val accountService = AccountServiceImpl(accountRepository)

    @Test
    fun `should return account when found by id`() {
        val accountId = 1L
        val expectedAccount = AccountEntity(
            name = "User1",
            fines = 0,
            active = true,
            id = accountId
        )

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount))
        val result = accountService.findByIdOrThrow(accountId)

        assertEquals(expectedAccount, result)
    }

}



