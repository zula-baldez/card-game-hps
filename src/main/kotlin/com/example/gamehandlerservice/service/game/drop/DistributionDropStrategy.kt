package com.example.gamehandlerservice.service.game.drop

import com.example.gamehandlerservice.model.dto.CardDropResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.exception.PlayerNotFoundException
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
        validatePlayers(request, gameHandler.gameData.cards)
        return if (gameHandler.gameData.cards[VirtualPlayers.TABLE.id]?.size == 1) {
            CardDropResult(
                changeTurn = true,
                valid = true,
                needsFine = false,
                nextStage = Stage.FINES
            )
        } else {
            gameHandler.gameData.cards.entries
                .filter { (playerId, _) -> playerId != request.toDropArea && playerId != VirtualPlayers.TABLE.id }
                .firstOrNull { (_, playerCards) -> isDropPrior(request.card, playerCards.last()) }
                ?.let {
                    CardDropResult.invalid
                } ?: CardDropResult(changeTurn = true, valid = true, needsFine = false, nextStage = null)
        }
    }

    override fun onDropEnemy(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        return if (gameHandler.gameData.cards[VirtualPlayers.TABLE.id]?.size == 1) {
            CardDropResult.invalid
        } else {
            if (isDropPrior(
                    request.card,
                    gameHandler.gameData.cards[request.toDropArea]?.lastOrNull() ?: return CardDropResult.invalid
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

    private fun validatePlayers(request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>) {
        require(cards.containsKey(request.toDropArea) && cards.containsKey(request.fromDropArea)) {
            throw PlayerNotFoundException()
        }
    }
}