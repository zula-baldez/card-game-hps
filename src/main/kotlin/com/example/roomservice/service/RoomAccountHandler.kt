package com.example.roomservice.service

import com.example.gamehandlerservice.model.dto.RoomAccountsOperationResult
import com.example.personalaccount.database.Account

interface RoomAccountHandler {
    fun addAccount(account: Account): RoomAccountsOperationResult
    fun kickAccount(id: Long): RoomAccountsOperationResult
    fun banAccount(id: Long): RoomAccountsOperationResult
    fun configure(capacity: Int)
}