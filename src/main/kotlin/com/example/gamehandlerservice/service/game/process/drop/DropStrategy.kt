package com.example.gamehandlerservice.service.game.process.drop

import com.example.gamehandlerservice.model.dto.CardDropValidationResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage

interface DropStrategy {
    var stage: Stage

    fun onDropYourself(
        turningPLayerId: Long, request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult

    fun onDropEnemy(
        turningPLayerId: Long, request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult

    fun onDropTable(
        turningPLayerId: Long, request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult

    fun validateDrop(
        turningPLayerId: Long, request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>
    ): CardDropValidationResult
}