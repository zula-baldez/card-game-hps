package com.example.gamehandlerservice.service.game.drop

import com.example.gamehandlerservice.model.dto.CardDropResult
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.CardCompareResult
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import org.springframework.stereotype.Component

@Component
class PlayingDropHandler() : DropStrategy {
    override var stage: Stage = Stage.PLAYING
    override fun onDropYourself(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        return CardDropResult.missClick
    }

    override fun onDropEnemy(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        return CardDropResult.invalid
    }

    override fun onDropTable(
        request: MoveCardRequest,
        gameHandler: GameHandler
    ): CardDropResult {
        if (gameHandler.gameData.cards[VirtualPlayers.TABLE.id]?.lastOrNull()
                ?.compareTo(gameHandler.gameData, request.card) == CardCompareResult.LESS
        ) {
            return CardDropResult.valid
        }
        return CardDropResult.invalid
    }
}
