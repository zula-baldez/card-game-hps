package com.example.gamehandlerservice.controllers

import com.example.common.dto.CreateGameRequest
import com.example.common.dto.CreateGameResponse
import com.example.gamehandlerservice.service.game.registry.GameHandlerRegistry
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameHandlerController(
    private val registry: GameHandlerRegistry
) {
    @PostMapping("/create")
    @Operation(summary = "create game")
    fun createGame(@RequestBody createGameRequest: CreateGameRequest): CreateGameResponse {
        val gameHandler = registry.createGame(createGameRequest.roomId)
        return CreateGameResponse(gameHandler.gameData.gameId)
    }
}