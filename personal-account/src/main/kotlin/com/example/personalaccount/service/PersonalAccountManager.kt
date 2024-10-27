package com.example.personalaccount.service

import com.example.common.dto.api.Pagination
import com.example.personalaccount.model.FriendshipDto
import org.springframework.data.domain.Page


interface PersonalAccountManager {
    fun addFriend(accountId: Long, friendId: Long)
    fun removeFriend(accountId: Long, friendId: Long)
    fun getAllFriends(accountId: Long, pagination: Pagination): Page<FriendshipDto>?
    fun addFine(accountId: Long)
}

