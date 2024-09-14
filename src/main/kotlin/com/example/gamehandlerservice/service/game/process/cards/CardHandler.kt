package com.example.gamehandlerservice.service.game.process.cards

import com.example.gamehandlerservice.database.Account
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card

interface CardHandler {

    fun moveCard(moveCardRequest: MoveCardRequest)
    fun startGame(players: List<Account>)
    val cards: MutableMap<Long, LinkedHashSet<Card>>
}