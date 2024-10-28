package com.example.gamehandlerservice

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.controllers.GameProcessController
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.game.GameHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class GameProcessControllerTest {

    private lateinit var gameProcessController: GameProcessController
    private lateinit var gameHandler: GameHandler

    @BeforeEach
    fun setUp() {
        gameHandler = mock(GameHandler::class.java)
        gameProcessController = GameProcessController()
    }

    @Test
    fun `should call moveCard on gameHandler`() {
        val accountDto = AccountDto(id = 2L, name = "Player1", fines = 0, roomId = 1L)
        val roomDto = RoomDto(id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf())
        val card = Card(Suit.DIAMONDS, 6L, false)
        val moveCardRequest = MoveCardRequest(fromDropArea = 1L, toDropArea = 2L, card = card)

        gameProcessController.moveCard(gameHandler, accountDto, roomDto, moveCardRequest)

        verify(gameHandler, times(1)).moveCard(moveCardRequest)
    }

    @Test
    fun `should call startGame on gameHandler`() {
        val accountDto = AccountDto(id = 2L, name = "Player1", fines = 0, roomId = 1L)
        val roomDto = RoomDto(id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf())

        gameProcessController.startGame(gameHandler, accountDto, roomDto)

        verify(gameHandler, times(1)).startGame()
    }
}