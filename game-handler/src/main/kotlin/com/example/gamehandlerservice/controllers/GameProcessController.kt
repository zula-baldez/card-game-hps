package com.example.gamehandlerservice.controllers

import com.example.common.aspects.HostOnly
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.model.dto.PlayerActionRequest
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
class GameProcessController(
    private val registry: GameHandlerRegistry,
    private val messagingTemplate: SimpMessageSendingOperations
) {
    @MessageMapping("/move")
    fun moveCard(
        @Valid accountDto: AccountDto,
        @Valid roomDto: RoomDto,
        @Valid @RequestBody playerActionRequest: PlayerActionRequest
    ) {
        registry.getGame(roomDto.id)?.handle(playerActionRequest.copy(playerId = accountDto.id))
    }

    @MessageMapping("/current-state")
    fun getFullState(@Valid roomDto: RoomDto) {
        val game =
            registry.getGame(roomDto.id) ?: throw IllegalArgumentException("Game ${roomDto.id} not found in registry")
        messagingTemplate.convertAndSend("/topic/room/${roomDto.id}/events", game.getGameState())
    }

    @HostOnly
    @MessageMapping("/start-game")
    fun startGame(
        @Valid accountDto: AccountDto,
        @Valid roomDto: RoomDto,
    ) {
        registry.getGame(roomDto.id)?.startGame()
    }
}