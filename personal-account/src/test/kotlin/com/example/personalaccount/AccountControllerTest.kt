package com.example.personalaccount

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.controllers.AccountController
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.PersonalAccountManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class AccountControllerTest {

    private lateinit var accountService: AccountService
    private lateinit var personalAccountManager: PersonalAccountManager
    private lateinit var accountController: AccountController

    @BeforeEach
    fun setUp() {
        accountService = mock()
        personalAccountManager = mock()
        accountController = AccountController(accountService, personalAccountManager)
    }
    @Test
    fun `should add fine to account`() {
        val accountId = 1L

        accountController.addFine(accountId)

        verify(personalAccountManager).addFine(accountId)
    }

    @Test
    fun `should return NOT_FOUND status when account not found`() {
        val accountId = 999L
        whenever(accountService.findByIdOrThrow(accountId)).thenThrow(AccountNotFoundException(accountId))

        val exception = assertThrows<AccountNotFoundException> {
            accountController.getAccountById(accountId)
        }
        assert(exception.message == "Account with id 999 not found")
    }
}