package com.example.gamehandlerservice.service.game.drop

import com.example.gamehandlerservice.model.dto.CardDropResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.util.VirtualPlayers

interface DropStrategy {
    var stage: Stage

    fun onDropYourself(
        request: MoveCardRequest, gameHandler: GameHandler
    ): CardDropResult

    fun onDropEnemy(
        request: MoveCardRequest, gameHandler: GameHandler
    ): CardDropResult

    fun onDropTable(
        request: MoveCardRequest, gameHandler: GameHandler
    ): CardDropResult

    fun validateDrop(
        request: MoveCardRequest, gameHandler: GameHandler
    ): CardDropResult {
        if (request.fromDropArea != VirtualPlayers.TABLE.id &&
            request.fromDropArea != VirtualPlayers.DECK.id &&
            request.fromDropArea != gameHandler.gameData.playersTurnQueue.current().id
        ) {
            return CardDropResult.invalid
        }
        if (request.fromDropArea == VirtualPlayers.TABLE.id) {
            return onDropTable(request, gameHandler)
        }
        if (gameHandler.gameData.playersTurnQueue.current().id == request.toDropArea) {
            return onDropYourself(request, gameHandler)
        }
        return onDropEnemy(request, gameHandler)
    }
}