package com.example.gamehandlerservice.service.game.util

import com.example.common.client.PersonalAccountClient
import com.example.common.dto.personalaccout.business.AccountDto
import com.example.gamehandlerservice.model.game.CardDropResult
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.springframework.stereotype.Component

@Component
class FinesCounter(
    val personalAccountClient: PersonalAccountClient
) {
    fun giveFine(
        gameHandler: GameHandler,
        accountFrom: AccountDto,
        accountTo: AccountDto,
        card: Card
    ): CardDropResult {
        val finesMap = gameHandler.gameData.finesCounter
            .getOrPut(accountTo.id) { mutableMapOf() }

        val currentFines = finesMap.getOrPut(accountFrom.id) { 0 }

        if (currentFines >= accountTo.fines) {
            return CardDropResult.invalid
        }

        finesMap[accountFrom.id] = currentFines + 1
//TODO remove fine
//        personalAccountClient.addFine()
//
//        accountTo.fines--
//
//        accountRepository.save(accountEntityTo)
        return CardDropResult.valid
    }
}