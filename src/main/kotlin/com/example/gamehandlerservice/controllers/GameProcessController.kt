package com.example.gamehandlerservice.controllers

import com.example.gamehandlerservice.aspects.HostOnly
import com.example.gamehandlerservice.aspects.TrueTurnValidation
import com.example.gamehandlerservice.database.Account
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.service.game.process.RoomHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@Controller
class GameProcessController : BaseController() {
    @TrueTurnValidation
    @MessageMapping("/move-card")
    fun moveCard(roomHandler: RoomHandler, account: Account, @RequestBody moveCardRequest: MoveCardRequest) {
        roomHandler.moveCard(moveCardRequest)
    }

    @HostOnly
    @MessageMapping("/start-game")
    fun startGame(roomHandler: RoomHandler, account: Account) {
        println("game start")
        roomHandler.startGame()
    }

    @MessageMapping("/test")
    fun test(roomHandler: RoomHandler, account: Account) {
        print(account)
    }

}