package com.example.personalaccount.controllers

import com.example.common.dto.personalaccout.Pagination
import com.example.personalaccount.model.FriendshipDto
import com.example.personalaccount.service.AccountService
import org.springframework.data.domain.Page
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@Validated
class PersonalAccountController(
    private val accountService: AccountService
) {
    @PostMapping("/")
    fun addFriend(accountId: Long, friendId: Long) {

    }
//    fun removeFriend(accountId: Long, friendId: Long)
//    fun getAllFriends(accountId: Long, pagination: Pagination): Page<FriendshipDto>?

}