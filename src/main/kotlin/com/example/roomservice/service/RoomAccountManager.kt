package com.example.roomservice.service

import com.example.gamehandlerservice.model.dto.AccountAction

interface RoomAccountManager {
    fun addAccount(roomId: Long, accountId: Long)
    fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction)
}