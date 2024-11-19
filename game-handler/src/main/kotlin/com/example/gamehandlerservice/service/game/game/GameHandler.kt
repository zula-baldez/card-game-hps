package com.example.gamehandlerservice.service.game.game

import com.example.gamehandlerservice.model.dto.GameStateResponse
import com.example.gamehandlerservice.model.dto.PlayerActionRequest

interface GameHandler {
    fun handle(playerAction: PlayerActionRequest)
    fun getGameState(): GameStateResponse
    fun startGame()
}