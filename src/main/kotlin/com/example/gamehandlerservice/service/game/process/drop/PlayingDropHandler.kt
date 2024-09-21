package com.example.gamehandlerservice.service.game.process.drop

import com.example.gamehandlerservice.model.dto.CardDropValidationResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.process.RoomHandler

class PlayingDropHandler() : DropStrategy {
    override var stage: Stage = Stage.PLAYING
    override fun onDropYourself(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>,
        roomHandler: RoomHandler
    ): CardDropValidationResult {
        //if (cards[request.fromDropArea]?.first(card -> card == ) != null)
        return CardDropValidationResult(false, false, false, nextStage = null)
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
        TODO("Not yet implemented")
    }
}
