package com.example.gamehandlerservice.service.game.model

import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.util.CyclicQueue

data class GameData(
    val gameId: Long,
    val roomId: Long,
    var trump: Suit?,
    var playersTurnQueue: CyclicQueue<AccountDto>,
    var userCards: MutableMap<Long, LinkedHashSet<Card>> = mutableMapOf(),
    val finesCounter: MutableMap<Long, MutableMap<Long, Int>> = mutableMapOf() //to id from id n times
)