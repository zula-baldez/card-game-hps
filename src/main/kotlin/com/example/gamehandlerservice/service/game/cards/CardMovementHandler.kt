package com.example.gamehandlerservice.service.game.cards

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.personalaccount.database.AccountEntity

interface CardMovementHandler {
    fun moveCard(moveCardRequest: MoveCardRequest, gameHandler: GameHandler)
    fun giveUsersBasicCards(players: List<AccountEntity>, gameHandler: GameHandler)
    fun clearPlayerCards(playerId: Long, gameHandler: GameHandler)
}