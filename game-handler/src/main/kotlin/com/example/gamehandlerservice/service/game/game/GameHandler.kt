package com.example.gamehandlerservice.service.game.game

import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.model.dto.GameStateResponse
import com.example.gamehandlerservice.model.dto.PlayerActionRequest

interface GameHandler {
    fun handle(playerAction: PlayerActionRequest)
    fun getGameState(): GameStateResponse
    fun startGame()
    fun setRoomId(roomId: Long)
    fun playerDisconnect(accountId: Long): AccountDto
    fun kickPlayer(accountId: Long): AccountDto
    fun addPlayer(account: AccountDto)
}