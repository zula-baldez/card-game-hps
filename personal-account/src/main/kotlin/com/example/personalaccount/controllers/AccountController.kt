package com.example.personalaccount.controllers

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.service.AccountService
import com.example.personalaccount.service.PersonalAccountManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService,
    private val personalAccountManager: PersonalAccountManager
) {

    @GetMapping("/{id}")
    fun getAccountById(@PathVariable id: Long): AccountDto {
        return accountService.findByIdOrThrow(id).toDto()
    }

    @PutMapping("/{id}/room")
    fun updateAccountRoom(@PathVariable id: Long, updateAccountRoomRequest: UpdateAccountRoomRequest): AccountDto {
        return accountService.updateAccountRoom(id, updateAccountRoomRequest.roomId).toDto()
    }

    @PostMapping("/{id}/fine")
    fun addFine(@PathVariable id: Long) {
        personalAccountManager.addFine(accountId = id)
    }

    @ExceptionHandler(AccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleAccountNotFound(ex: AccountNotFoundException): String {
        return ex.message ?: "Account not found"
    }

}
