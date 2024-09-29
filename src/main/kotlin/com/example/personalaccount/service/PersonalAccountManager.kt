package com.example.personalaccount.service

import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.model.FriendshipStatus
import java.util.*


interface PersonalAccountManager {
    fun addFriend(userId: Long, friendId: Long)
    fun removeFriend(userId: Long, friendId: Long)
    fun getAllFriends(userId: Long): Optional<MutableSet<AccountEntity>>?
    fun addFine(account: Long)
    fun getInRoomAccounts() : List<AccountEntity>
}