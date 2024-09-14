package com.example.gamehandlerservice.service.game.process.account

import com.example.gamehandlerservice.database.Account
import com.example.gamehandlerservice.model.dto.RoomAccountsOperationResult

interface RoomAccountHandler {
    fun addAccount(account: Account): RoomAccountsOperationResult
    fun kickAccount(id: Long): RoomAccountsOperationResult
    fun banAccount(id: Long): RoomAccountsOperationResult
    fun configure(capacity: Int)
    fun addFine(account: Long)
    fun getAccounts() : List<Account>
}