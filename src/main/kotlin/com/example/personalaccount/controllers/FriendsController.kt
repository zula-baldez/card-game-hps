package com.example.personalaccount.controllers

import com.example.common.dto.api.Pagination
import com.example.common.exceptions.AccountNotFoundException
import com.example.personalaccount.exceptions.AddFriendException
import com.example.personalaccount.exceptions.RemoveFriendException
import com.example.personalaccount.model.AddFriendRequest
import com.example.personalaccount.model.FriendshipDto
import com.example.personalaccount.service.PersonalAccountManager
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class FriendsController(
    val accountsManager: PersonalAccountManager
) {
    @GetMapping("/friends")
    fun getFriends(pagination: Pagination, response: HttpServletResponse, auth: Principal): List<FriendshipDto> {
        val result = accountsManager.getAllFriends(auth.name.toLong(), pagination) ?: throw AccountNotFoundException(auth.name.toLong())
        response.setIntHeader("x-total-friends", result.totalElements.toInt())
        return result.toList()
    }

    @PostMapping("/friends")
    fun sendOrAcceptRequest(@RequestBody @Valid addFriendRequest: AddFriendRequest, auth: Principal) {
        return accountsManager.addFriend(auth.name.toLong(), addFriendRequest.friendId)
    }

    @DeleteMapping("/friends/{friendId}")
    fun removeFriendOrRequest(@PathVariable friendId: Long, auth: Principal) {
        return accountsManager.removeFriend(auth.name.toLong(), friendId)
    }

    @ExceptionHandler(AddFriendException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnauthorized(ex: AddFriendException): String {
        return ex.message ?: "Failed to add friend"
    }

    @ExceptionHandler(RemoveFriendException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnauthorized(ex: RemoveFriendException): String {
        return ex.message ?: "Failed to remove friend"
    }

    @ExceptionHandler(AccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleFriendNotFound(ex: AccountNotFoundException): String {
        return ex.message ?: "Friends not found"
    }
}