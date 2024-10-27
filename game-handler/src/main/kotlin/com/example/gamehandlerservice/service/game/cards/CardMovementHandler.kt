package com.example.gamehandlerservice.service.game.cards

import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.service.game.game.GameHandler

interface CardMovementHandler {
    fun moveCard(moveCardRequest: MoveCardRequest, gameHandler: GameHandler)
    fun giveUsersBasicCards(players: List<AccountDto>, gameHandler: GameHandler)
    fun clearPlayerCards(playerId: Long, gameHandler: GameHandler)
}