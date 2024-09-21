package com.example.gamehandlerservice.service.game.process.drop

import com.example.gamehandlerservice.model.dto.CardDropValidationResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.process.RoomHandler

interface DropStrategy {
    var stage: Stage

    fun onDropYourself(
        turningPLayerId: Long, request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>, roomHandler: RoomHandler
    ): CardDropValidationResult

    fun onDropEnemy(
        turningPLayerId: Long, request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>, roomHandler: RoomHandler
    ): CardDropValidationResult

    fun onDropTable(
        turningPLayerId: Long, request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>, roomHandler: RoomHandler
    ): CardDropValidationResult

    fun validateDrop(
        turningPLayerId: Long, request: MoveCardRequest, cards: Map<Long, LinkedHashSet<Card>>, roomHandler: RoomHandler
    ): CardDropValidationResult {
        if (request.fromDropArea == -1L) {
            return onDropTable(turningPLayerId, request, cards, roomHandler)
        }
        if (turningPLayerId == request.toDropArea) {
            return onDropYourself(turningPLayerId, request, cards, roomHandler)
        }
        return onDropEnemy(turningPLayerId, request, cards, roomHandler)
    }
}