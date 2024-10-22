package com.example.personalaccount.service

import com.example.common.dto.api.Pagination
import com.example.common.dto.business.AccountDto
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.model.FriendshipDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface PersonalAccountManager {
    fun addFriend(accountId: Long, friendId: Long)
    fun removeFriend(accountId: Long, friendId: Long)
    fun getAllFriends(accountId: Long, pagination: Pagination): Page<FriendshipDto>?
    fun addFine(accountId: Long)
}