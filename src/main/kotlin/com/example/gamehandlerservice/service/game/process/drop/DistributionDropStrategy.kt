package com.example.gamehandlerservice.service.game.process.drop

import com.example.gamehandlerservice.model.dto.CardDropValidationResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.exception.PlayerNotFoundException
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import org.springframework.stereotype.Component


@Component
class DistributionDropStrategy : DropStrategy {

    override var stage: Stage = Stage.DISTRIBUTION
    private val maxStrength = 14L
    private val tableId = -1L

    override fun onDropYourself(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult {
        require(cards.containsKey(request.toDropArea)) { throw PlayerNotFoundException() }

        if (cards[tableId]!!.size == 1) {
            return CardDropValidationResult(
                changeTurn = true,
                valid = true,
                needsFine = false,
                needToChangeStage = true
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
                    needToChangeStage = false
                )
            }
        }
        return CardDropValidationResult(changeTurn = true, valid = true, needsFine = false, needToChangeStage = false)
    }

    override fun onDropEnemy(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult {
        require(cards.containsKey(request.toDropArea) && cards.containsKey(request.fromDropArea)) { throw PlayerNotFoundException() }
        if (cards[tableId]!!.size == 1) {
            return CardDropValidationResult(
                changeTurn = true,
                valid = false,
                needsFine = true,
                needToChangeStage = false
            )
        }
        val lastEnemyCard = cards[request.toDropArea]!!.last()
        val card = request.card
        if (lastEnemyCard.strenght == card.strenght - 1 ||
            request.card.strenght == 2L && lastEnemyCard.strenght == maxStrength) {
            return CardDropValidationResult(
                changeTurn = false,
                valid = true,
                needsFine = false,
                needToChangeStage = false
            )
        }
        return CardDropValidationResult(changeTurn = true, valid = false, needsFine = true, needToChangeStage = false)
    }

    override fun onDropTable(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult {
        return CardDropValidationResult(changeTurn = false, valid = false, needsFine = false, needToChangeStage = false)
    }

    override fun validateDrop(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult {
        if(request.fromDropArea != -1L) {
            return CardDropValidationResult(
                changeTurn = false,
                valid = false,
                needsFine = false,
                needToChangeStage = false
            )
        }

        if (turningPLayerId == request.toDropArea) {
            return onDropYourself(turningPLayerId, request, cards)
        }
        return onDropEnemy(turningPLayerId, request, cards)
    }


}