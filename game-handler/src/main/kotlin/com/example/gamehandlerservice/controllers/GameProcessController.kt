package com.example.gamehandlerservice.controllers

import com.example.common.aspects.HostOnly
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.model.dto.PlayerActionRequest
import com.example.gamehandlerservice.service.game.game.GameHandler
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
class GameProcessController {
    @MessageMapping("/move")
    fun moveCard(
        gameHandler: GameHandler,
        @Valid accountDto: AccountDto,
        @Valid roomDto: RoomDto,
        @Valid @RequestBody playerActionRequest: PlayerActionRequest
    ) {
        gameHandler.handle(playerActionRequest.copy(playerId = accountDto.id))
    }

    @HostOnly
    @MessageMapping("/start-game")
    fun startGame(
        gameHandler: GameHandler,
        @Valid accountDto: AccountDto,
        @Valid roomDto: RoomDto,
    ) {
        gameHandler.startGame()
    }
}