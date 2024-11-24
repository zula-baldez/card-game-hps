package com.example.personalaccount

import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.controllers.AccountController
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.AvatarsHandler
import com.example.personalaccount.service.PersonalAccountManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AccountControllerTest {

    private lateinit var accountService: AccountService
    private lateinit var personalAccountManager: PersonalAccountManager
    private lateinit var accountController: AccountController
    private lateinit var avatarsHandler: AvatarsHandler

    @BeforeEach
    fun setUp() {
        accountService = mock()
        personalAccountManager = mock()
        avatarsHandler = mock()
        accountController = AccountController(accountService, personalAccountManager, avatarsHandler)
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