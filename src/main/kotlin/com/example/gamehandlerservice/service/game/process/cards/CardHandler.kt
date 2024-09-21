package com.example.gamehandlerservice.service.game.process.cards

import com.example.personalaccount.database.Account
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit

interface CardHandler {
    fun moveCard(moveCardRequest: MoveCardRequest)
    fun giveUsersBasicCards(players: List<Account>)
    val cards: MutableMap<Long, LinkedHashSet<Card>>
    var trump: Suit
}