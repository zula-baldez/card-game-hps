package com.example.gamehandlerservice.controllers

import com.example.gamehandlerservice.aspects.HostOnly
import com.example.gamehandlerservice.aspects.TrueTurnValidation
import com.example.personalaccount.database.Account
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody


@Controller
class GameProcessController : BaseController() {
    @TrueTurnValidation
    @MessageMapping("/move-card")
    fun moveCard(gameHandler: GameHandler, account: Account, @RequestBody moveCardRequest: MoveCardRequest) {
        gameHandler.moveCard(moveCardRequest)
    }

    @HostOnly
    @MessageMapping("/start-game")
    fun startGame(gameHandler: GameHandler, account: Account) {
        gameHandler.startGame()
    }

    @MessageMapping("/test")
    fun test(gameHandler: GameHandler, account: Account) {
        print(account)
    }

}