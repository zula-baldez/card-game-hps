package com.example.gamehandlerservice.service.game.util

import com.example.gamehandlerservice.model.game.CardDropResult
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import org.springframework.stereotype.Component

@Component
class FinesCounter(val accountRepository: AccountRepository) {
    fun giveFine(gameHandler: GameHandler, accountEntityFrom: AccountEntity, accountEntityTo: AccountEntity, card: Card): CardDropResult {
        val finesMap = gameHandler.gameData.finesCounter
            .getOrPut(accountEntityTo.id) { mutableMapOf() }

        val currentFines = finesMap.getOrPut(accountEntityFrom.id) { 0 }

        if (currentFines >= accountEntityTo.fines) {
            return CardDropResult.invalid
        }

        finesMap[accountEntityFrom.id] = currentFines + 1
        accountEntityTo.fines--
        accountRepository.save(accountEntityTo)
        return CardDropResult.valid
    }
}