package com.example.gamehandlerservice.service.game.model

import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import com.example.personalaccount.database.Account

data class GameData(
    val gameId: Long,
    val roomId: Long,
    val trump: Suit?,
    var playersTurnQueue: CyclicQueue<Account>,
    val cards: Map<Long, LinkedHashSet<Card>> = mutableMapOf(),
    val finesCounter: MutableMap<Long, MutableMap<Long, Int>> = mutableMapOf() //to id from id n times
)