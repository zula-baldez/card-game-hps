package com.example.gamehandlerservice.service.game.game

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.model.dto.GameStateResponse
import com.example.gamehandlerservice.model.dto.PlayerActionRequest

interface GameHandler {
    fun handle(playerAction: PlayerActionRequest)
    fun getGameState(): GameStateResponse
    fun startGame()
    fun setRoom(room: RoomDto)
    fun updateHostId(newHostId: Long)
    fun addPlayer(account: AccountDto)
    fun removePlayer(accountId: Long)
}