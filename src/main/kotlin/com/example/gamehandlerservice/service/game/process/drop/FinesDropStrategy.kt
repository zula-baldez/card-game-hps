package com.example.gamehandlerservice.service.game.process.drop

import com.example.gamehandlerservice.model.dto.CardDropValidationResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.process.RoomHandler
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class FinesDropStrategy(
) : DropStrategy {
    override var stage: Stage = Stage.FINES

    override fun onDropYourself(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>,
        roomHandler: RoomHandler
    ): CardDropValidationResult {
        return CardDropValidationResult(
            changeTurn = false,
            valid = false,
            needsFine = false,
            nextStage = null
        )
    }

    override fun onDropEnemy(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>,
        roomHandler: RoomHandler
    ): CardDropValidationResult {
        TODO("Not yet implemented")
    }

    override fun onDropTable(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>,
        roomHandler: RoomHandler
    ): CardDropValidationResult {
        return CardDropValidationResult(
            changeTurn = false,
            valid = false,
            needsFine = true,
            nextStage = null,
        )
    }
}