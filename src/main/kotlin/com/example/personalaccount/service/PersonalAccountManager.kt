package com.example.personalaccount.service

import com.example.personalaccount.database.Account


interface PersonalAccountManager {
    fun addFriend(userId: Long, friendId: Long)
    fun removeFriend(userId: Long, friendId: Long)
    fun getAllFriends(userId: Long): Set<Account>?
    fun addFine(account: Long)
    fun getInRoomAccounts() : List<Account>
}