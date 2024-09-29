package com.example.gamehandlerservice.controllers

import com.example.common.aspects.HostOnly
import com.example.common.aspects.TrueTurnValidation
import com.example.personalaccount.database.AccountEntity
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody


@Controller
class GameProcessController {
    @TrueTurnValidation
    @MessageMapping("/move-card")
    fun moveCard(gameHandler: GameHandler, accountEntity: AccountEntity, @RequestBody moveCardRequest: MoveCardRequest) {
        gameHandler.moveCard(moveCardRequest)
    }

    @HostOnly
    @MessageMapping("/start-game")
    fun startGame(gameHandler: GameHandler, accountEntity: AccountEntity) {
        gameHandler.startGame()
    }

    @MessageMapping("/test")
    fun test(gameHandler: GameHandler, accountEntity: AccountEntity) {
        print(accountEntity)
    }

}