package com.example.gamehandlerservice.service.game.process.drop

import com.example.gamehandlerservice.model.dto.CardDropValidationResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage

class PlayingDropHandler() : DropStrategy {
    override var stage: Stage = Stage.PLAYING
    override fun onDropYourself(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult {
        TODO("Not yet implemented")
    }

    override fun onDropEnemy(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult {
        TODO("Not yet implemented")
    }

    override fun onDropTable(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult {
        TODO("Not yet implemented")
    }

    override fun validateDrop(
        turningPLayerId: Long,
        request: MoveCardRequest,
        cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult {
        TODO("Not yet implemented")
    }

}
