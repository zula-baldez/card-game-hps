package com.example.gamehandlerservice.controllers

import com.example.common.aspects.HostOnly
import com.example.common.aspects.TrueTurnValidation
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.personalaccount.database.AccountEntity
import com.example.roomservice.repository.RoomEntity
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
class GameProcessController {
    @TrueTurnValidation
    @MessageMapping("/move-card")
    fun moveCard(
        gameHandler: GameHandler,
        @Valid accountEntity: AccountEntity,
        @Valid roomEntity: RoomEntity,
        @Valid @RequestBody moveCardRequest: MoveCardRequest
    ) {
        gameHandler.moveCard(moveCardRequest)
    }

    @HostOnly
    @MessageMapping("/start-game")
    fun startGame(
        gameHandler: GameHandler, @Valid accountEntity: AccountEntity, @Valid roomEntity: RoomEntity,
    ) {
        gameHandler.startGame()
    }
}