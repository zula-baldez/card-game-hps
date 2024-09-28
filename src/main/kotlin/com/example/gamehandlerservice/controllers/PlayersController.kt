package com.example.gamehandlerservice.controllers

import com.example.gamehandlerservice.aspects.HostOnly
import com.example.gamehandlerservice.model.dto.AccountActionRequest
import com.example.personalaccount.database.Account
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.repository.Room
import com.example.roomservice.service.RoomAccountManager
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
class PlayersController(
    private val roomAccountManager: RoomAccountManager,
    private val accountRepository: AccountRepository
) : BaseController() {
//    @HostOnly
//    @MessageMapping("/kick")
//    fun kickPlayer(
//        room: Room, account: Account, @RequestBody accountActionRequest: AccountActionRequest
//    ) {
//        roomAccountManager.kickAccount(
//            room.id ?: return,
//            accountRepository.findById(accountActionRequest.accountId).orElse(null) ?: return
//        )
//    }
//
//    @HostOnly
//    @MessageMapping("/ban")
//    fun banPlayer(room: Room, account: Account, @RequestBody accountActionRequest: AccountActionRequest) {
//        roomAccountManager.banAccount(
//            room.id ?: return,
//            accountRepository.findById(accountActionRequest.accountId).orElse(null) ?: return
//        )
//    }
}