package com.example.personalaccount.controllers

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.PersonalAccountManager
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/accounts")
@Tag(name = "account_controller", description = "Rest API for accounts")
class AccountController(
    private val accountService: AccountService,
    private val personalAccountManager: PersonalAccountManager
) {

    @GetMapping("/{id}")
    @Operation(summary = "Get account by id")
    fun getAccountById(@PathVariable id: Long): AccountDto {
        return accountService.findByIdOrThrow(id).toDto()
    }

    @PostMapping("")
    @Operation(summary = "Create account")
    fun createAccount(@RequestBody createAccountDto: CreateAccountDto): AccountDto {
        return accountService.createAccountForUser(createAccountDto).toDto()
    }

    @PutMapping("/{id}/room")
    @Operation(summary = "Update account room")
    fun updateAccountRoom(@PathVariable id: Long, @RequestBody updateAccountRoomRequest: UpdateAccountRoomRequest): AccountDto {
        return accountService.updateAccountRoom(id, updateAccountRoomRequest.roomId).toDto()
    }

    @PostMapping("/{id}/fine")
    @Operation(summary = "Add fine to account")
    fun addFine(@PathVariable id: Long) {
        personalAccountManager.addFine(accountId = id)
    }

    @ExceptionHandler(AccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleAccountNotFound(ex: AccountNotFoundException): String {
        return ex.message ?: "Account not found"
    }

}
