package com.example.roomservice.service

import com.example.gamehandlerservice.model.dto.AccountAction
import com.example.roomservice.dto.RoomAccountActionResult

interface RoomAccountManager {
    fun addAccount(roomId: Long, accountId: Long): RoomAccountActionResult
    fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction): RoomAccountActionResult
}