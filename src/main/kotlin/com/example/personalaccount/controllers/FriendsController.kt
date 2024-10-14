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
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class FriendsController(
    val accountsManager: PersonalAccountManager
) {
    @GetMapping("/friends")
    fun getFriends(pagination: Pagination, response: HttpServletResponse, @RequestHeader("x-user-id") userId: Long): List<FriendshipDto> {
        val result = accountsManager.getAllFriends(userId, pagination) ?: throw AccountNotFoundException(userId)
        response.setIntHeader("x-total-friends", result.totalElements.toInt())
        return result.toList()
    }

    @PostMapping("/friends")
    @ResponseStatus(HttpStatus.CREATED)
    fun sendOrAcceptRequest(@RequestBody @Valid addFriendRequest: AddFriendRequest, @RequestHeader("x-user-id") userId: Long) {
        return accountsManager.addFriend(userId, addFriendRequest.friendId)
    }

    @DeleteMapping("/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeFriendOrRequest(@PathVariable friendId: Long, @RequestHeader("x-user-id") userId: Long) {
        return accountsManager.removeFriend(userId, friendId)
    }

    @ExceptionHandler(AddFriendException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleAddFriendException(ex: AddFriendException): String {
        return ex.message ?: "Failed to add friend"
    }

    @ExceptionHandler(RemoveFriendException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRemoveFriendException(ex: RemoveFriendException): String {
        return ex.message ?: "Failed to remove friend"
    }

    @ExceptionHandler(AccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleFriendNotFound(ex: AccountNotFoundException): String {
        return ex.message ?: "Friends not found"
    }
}