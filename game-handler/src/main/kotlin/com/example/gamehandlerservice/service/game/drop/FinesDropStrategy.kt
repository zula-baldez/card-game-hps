package com.example.gamehandlerservice.service.game.drop

import com.example.common.client.PersonalAccountClient
import com.example.gamehandlerservice.model.game.CardDropResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.util.FinesCounter
import org.springframework.stereotype.Component

@Component
class FinesDropStrategy(
    private val finesCounter: FinesCounter,
    private val personalAccountClient: PersonalAccountClient
) : DropStrategy {
    override var stage: Stage = Stage.FINES

    override fun onDropYourself(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        return CardDropResult.missClick
    }

    override fun onDropEnemy(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        if (request.fromDropArea != gameHandler.gameData.playersTurnQueue.current().id) {
            return CardDropResult.invalid
        }
        val fromAccount = personalAccountClient.getAccountById(gameHandler.gameData.playersTurnQueue.current().id)
        val toAccount = personalAccountClient.getAccountById(request.toDropArea)

        return finesCounter.giveFine(
            gameHandler,
            fromAccount,
            toAccount,
            request.card
        )
    }

    override fun onDropTable(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        return CardDropResult.invalid
    }
}