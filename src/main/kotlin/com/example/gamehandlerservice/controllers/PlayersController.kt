package com.example.gamehandlerservice.controllers

import com.example.gamehandlerservice.aspects.HostOnly
import com.example.gamehandlerservice.model.dto.AccountActionRequest
import com.example.personalaccount.database.Account
import com.example.personalaccount.database.AccountRepo
import com.example.roomservice.repository.Room
import com.example.roomservice.service.RoomAccountHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
class PlayersController(
    private val roomAccountHandler: RoomAccountHandler,
    private val accountRepo: AccountRepo
) : BaseController() {
    @HostOnly
    @MessageMapping("/kick")
    fun kickPlayer(
        room: Room, account: Account, @RequestBody accountActionRequest: AccountActionRequest
    ) {
        roomAccountHandler.kickAccount(
            room.id ?: return,
            accountRepo.findById(accountActionRequest.accountId).orElse(null) ?: return
        )
    }

    @HostOnly
    @MessageMapping("/ban")
    fun banPlayer(room: Room, account: Account, @RequestBody accountActionRequest: AccountActionRequest) {
        roomAccountHandler.banAccount(
            room.id ?: return,
            accountRepo.findById(accountActionRequest.accountId).orElse(null) ?: return
        )
    }
}