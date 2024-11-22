package com.example.gamehandlerservice.model.dto

import com.example.common.dto.personalaccout.AccountDto
import com.example.gamehandlerservice.model.game.Card

data class GameStateResponse(
    val table: List<Card>,
    val state: GameState,
    val trumpCard: Card?,
    val deckSize: Int,
    val stage: GameStage,
    val winner: Long?,
    val players: List<AccountDto>
)
