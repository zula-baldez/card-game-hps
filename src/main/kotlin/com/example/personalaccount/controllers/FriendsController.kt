package com.example.personalaccount.controllers

import com.example.personalaccount.database.Account
import com.example.personalaccount.exceptions.FriendNotFoundException
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

    @GetMapping("/get-friends")
    fun getFriends(auth: Principal): Optional<MutableSet<Account>>? {
        return friendsManagerImpl.getAllFriends(getAuthenticatedUserId(auth))
    }

    @GetMapping("/allow-friend")
    fun allowFriendship(@RequestParam friendId: Long, auth: Principal): ResponseEntity<String> {
        friendsManagerImpl.addFriend(getAuthenticatedUserId(auth), friendId)
        return ResponseEntity.ok("Friendship with user ID $friendId has been allowed.")
    }

    @DeleteMapping("/deny-friend")
    fun denyFriendship(@RequestParam friendId: Long, auth: Principal): ResponseEntity<String> {
        friendsManagerImpl.removeFriend(getAuthenticatedUserId(auth), friendId)
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


    private fun getUserIdFromPrincipal(auth: Principal): Long {
        return auth.name.toLong()
    }

    private fun getAuthenticatedUserId(auth: Principal): Long {
        return getUserIdFromPrincipal(auth)
    }
}