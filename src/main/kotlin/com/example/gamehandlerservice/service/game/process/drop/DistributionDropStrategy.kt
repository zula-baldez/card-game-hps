package com.example.gamehandlerservice.service.game.process.drop

import com.example.gamehandlerservice.model.dto.CardDropValidationResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.exception.PlayerNotFoundException
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.process.RoomHandler
import org.springframework.stereotype.Component

@Component
class DistributionDropStrategy : DropStrategy {

    override var stage: Stage = Stage.DISTRIBUTION
    private val maxStrength = 14L
    private val tableId = -1L

    override fun onDropYourself(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>,
        roomHandler: RoomHandler
    ): CardDropValidationResult {
        require(cards.containsKey(request.toDropArea)) { throw PlayerNotFoundException() }

        if (cards[tableId]!!.size == 1) {
            return CardDropValidationResult(
                changeTurn = true,
                valid = true,
                needsFine = false,
                nextStage = Stage.FINES
            )
        }
        for (accountId in cards.keys) {
            if (accountId == request.toDropArea || accountId == tableId) {
                continue
            }

            val lastEnemyCard = cards[accountId]!!.last()
            if (lastEnemyCard.strenght == request.card.strenght - 1 ||
                request.card.strenght == 2L && lastEnemyCard.strenght == maxStrength
            ) {
                return CardDropValidationResult(
                    changeTurn = true,
                    valid = false,
                    needsFine = true,
                    nextStage = null
                )
            }
        }
        return CardDropValidationResult(changeTurn = true, valid = true, needsFine = false, nextStage = null)
    }

    override fun onDropEnemy(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>,
        roomHandler: RoomHandler
    ): CardDropValidationResult {
        require(cards.containsKey(request.toDropArea) && cards.containsKey(request.fromDropArea)) { throw PlayerNotFoundException() }
        if (cards[tableId]!!.size == 1) {
            return CardDropValidationResult(
                changeTurn = true,
                valid = false,
                needsFine = true,
                nextStage = null
            )
        }
        val lastEnemyCard = cards[request.toDropArea]!!.last()
        val card = request.card
        if (lastEnemyCard.strenght == card.strenght - 1 ||
            request.card.strenght == 2L && lastEnemyCard.strenght == maxStrength
        ) {
            return CardDropValidationResult(
                changeTurn = false,
                valid = true,
                needsFine = false,
                nextStage = null
            )
        }
        return CardDropValidationResult(changeTurn = true, valid = false, needsFine = true, nextStage = null)
    }

    override fun onDropTable(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>,
        roomHandler: RoomHandler
    ): CardDropValidationResult {
        return CardDropValidationResult(changeTurn = false, valid = false, needsFine = false, nextStage = null)
    }
}