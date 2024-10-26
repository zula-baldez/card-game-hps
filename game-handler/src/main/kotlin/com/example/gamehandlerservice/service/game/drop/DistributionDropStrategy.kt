package com.example.gamehandlerservice.service.game.drop

import com.example.gamehandlerservice.model.game.CardDropResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import org.springframework.stereotype.Component

@Component
class DistributionDropStrategy : DropStrategy {

    override var stage: Stage = Stage.DISTRIBUTION
    private val maxStrength = 14L

    override fun onDropYourself(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        return if (gameHandler.gameData.userCards[VirtualPlayers.TABLE.id]?.size == 1) {
            gameHandler.gameData.trump = gameHandler.gameData.userCards[VirtualPlayers.TABLE.id]?.first()?.suit
            CardDropResult(
                changeTurn = true,
                valid = true,
                needsFine = false
            )
        } else {
            gameHandler.gameData.userCards.entries
                .filter { (playerId, _) -> playerId != request.toDropArea && playerId != VirtualPlayers.TABLE.id }
                .firstOrNull { (_, playerCards) -> isDropPrior(request.card, playerCards.last()) }
                ?.let {
                    CardDropResult.invalid
                } ?: CardDropResult(changeTurn = true, valid = true, needsFine = false)
        }
    }

    override fun onDropEnemy(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        return if (gameHandler.gameData.userCards[VirtualPlayers.TABLE.id]?.size == 1) {
            CardDropResult.invalid
        } else {
            if (isDropPrior(
                    request.card,
                    gameHandler.gameData.userCards[request.toDropArea]?.lastOrNull() ?: return CardDropResult.invalid
                )
            ) {
                CardDropResult.valid
            } else {
                CardDropResult.invalid
            }
        }
    }

    override fun onDropTable(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        return CardDropResult.missClick
    }

    private fun isDropPrior(card: Card, otherCard: Card): Boolean {
        return otherCard.strenght == card.strenght - 1 || (card.strenght == 2L && otherCard.strenght == maxStrength)
    }
}