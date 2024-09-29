package com.example.personalaccount.controllers

import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.exceptions.FriendNotFoundException
import com.example.personalaccount.model.AddFriendRequest
import com.example.personalaccount.service.PersonalAccountManagerImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*

@RestController
class FriendsController(
    val friendsManagerImpl: PersonalAccountManagerImpl
) {

    @GetMapping("/friends")
    fun getFriends(auth: Principal): Optional<MutableSet<AccountEntity>>? {
        return friendsManagerImpl.getAllFriends(auth.name.toLong())
    }

    @PostMapping("/friends")
    fun allowFriendship(@RequestBody addFriendRequest: AddFriendRequest, auth: Principal): ResponseEntity<String> {
        friendsManagerImpl.addFriend(auth.name.toLong(), addFriendRequest.friendId)
        return ResponseEntity.ok("Friendship with user ID ${addFriendRequest.friendId} has been allowed.")
    }

    @DeleteMapping("/friends/{friendId}")
    fun denyFriendship(@PathVariable friendId: Long, auth: Principal): ResponseEntity<String> {
        friendsManagerImpl.removeFriend(auth.name.toLong(), friendId)
        return ResponseEntity.ok("Friendship with user ID $friendId has been denied.")
    }

    @ExceptionHandler(ResponseStatusException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorized(ex: ResponseStatusException): String {
        return ex.message
    }

    @ExceptionHandler(FriendNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleFriendNotFound(ex: FriendNotFoundException): String {
        return ex.message ?: "Friends not found"
    }
}