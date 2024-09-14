package com.example.gamehandlerservice.controllers

import com.example.gamehandlerservice.aspects.HostOnly
import com.example.gamehandlerservice.database.Account
import com.example.gamehandlerservice.model.dto.AccountActionRequest
import com.example.gamehandlerservice.service.game.process.RoomHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
class PlayersController : BaseController() {
    @HostOnly
    @MessageMapping("/kick")
    fun kickPlayer(
        roomHandler: RoomHandler, account: Account, @RequestBody accountActionRequest: AccountActionRequest
    ) {
        roomHandler.kickAccount(accountActionRequest.accountId)
    }

    @HostOnly
    @MessageMapping("/ban")
    fun banPlayer(roomHandler: RoomHandler, account: Account, @RequestBody accountActionRequest: AccountActionRequest) {
        roomHandler.banAccount(accountActionRequest.accountId)
    }
}