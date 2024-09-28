package com.example.personalaccount.service

import com.example.personalaccount.database.Account
import com.example.personalaccount.model.FriendshipStatus
import java.util.*


interface PersonalAccountManager {
    fun addFriend(userId: Long, friendId: Long): FriendshipStatus
    fun removeFriend(userId: Long, friendId: Long): FriendshipStatus
    fun getAllFriends(userId: Long): Optional<MutableSet<Account>>?
    fun addFine(account: Long)
    fun getInRoomAccounts() : List<Account>
}