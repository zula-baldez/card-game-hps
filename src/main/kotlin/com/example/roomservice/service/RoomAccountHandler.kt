package com.example.roomservice.service

import com.example.gamehandlerservice.model.dto.RoomAccountsOperationResult
import com.example.personalaccount.database.Account

interface RoomAccountHandler {
    fun addAccount(roomId: Long, account: Account): RoomAccountsOperationResult
    fun kickAccount(roomId: Long, account: Account): RoomAccountsOperationResult
    fun banAccount(roomId: Long, account: Account): RoomAccountsOperationResult
}