package com.example.gamehandlerservice.service.game.util

import com.example.gamehandlerservice.model.dto.CardDropResult
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.personalaccount.database.AccountEntity
import org.springframework.stereotype.Component

@Component
class FinesCounter {
    fun giveFine(gameHandler: GameHandler, accountEntityFrom: AccountEntity, accountEntityTo: AccountEntity, card: Card): CardDropResult {
        val finesMap = gameHandler.gameData.finesCounter
            .getOrPut(accountEntityTo.id) { mutableMapOf() }

        val currentFines = finesMap.getOrPut(accountEntityFrom.id) { 0 }

        if (currentFines >= accountEntityTo.fines) {
            return CardDropResult.invalid
        }

        finesMap[accountEntityFrom.id] = currentFines + 1

        val totalFines = gameHandler.gameData.finesCounter.values.flatMap { it.values }.sum()
        val totalPlayerFines = gameHandler.gameData.playersTurnQueue.getAll().sumOf { it.fines }

        if (totalFines ==
            totalPlayerFines
        ) {
            return CardDropResult(
                changeTurn = true, valid = true, needsFine = false, nextStage = Stage.PLAYING
            )
        }
        return CardDropResult.valid
    }
}