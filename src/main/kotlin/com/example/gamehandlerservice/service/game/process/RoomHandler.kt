package com.example.gamehandlerservice.service.game.process

import com.example.gamehandlerservice.database.Account
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.dto.RoomAccountsOperationResult
import com.example.gamehandlerservice.model.game.Stage

interface RoomHandler {
    val id: Long
    val hostId: Long
    val name: String
    val count: Long
    val capacity: Int

    fun addAccount(account: Account) : RoomAccountsOperationResult
    fun kickAccount(id: Long) : RoomAccountsOperationResult
    fun banAccount(id: Long) : RoomAccountsOperationResult

    fun moveCard(moveCardRequest: MoveCardRequest)

    fun startGame()

    fun configureGameHandler(name: String, stage: Stage, id: Long, hostId: Long, capacity: Int)

    fun turningPlayerId() : Long

    fun getAllPlayers() : List<Account>
}