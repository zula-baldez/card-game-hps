package com.example.personalaccount.controllers

import com.example.common.dto.personalaccout.business.AccountDto
import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.database.AccountRepository
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.PersonalAccountManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountRepository: AccountRepository,
    private val personalAccountManager: PersonalAccountManager
) {

    @GetMapping("/{id}")
    fun getAccountById(@PathVariable id: Long): ResponseEntity<AccountDto> {
        val account = accountRepository.findById(id).getOrNull() ?: throw AccountNotFoundException(accountId = id)
        return ResponseEntity.ok(account.toDto())
    }


    @PostMapping("/{id}/fine")
    fun addFine(@PathVariable id: Long) {
        val account = accountRepository.findById(id).getOrNull() ?: throw AccountNotFoundException(accountId = id)
        personalAccountManager.addFine(accountId = id)
    }

    @ExceptionHandler(AccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleAccountNotFound(ex: AccountNotFoundException): String {
        return ex.message ?: "Account not found"
    }

}
