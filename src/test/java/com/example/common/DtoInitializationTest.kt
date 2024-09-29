package com.example.common

import com.example.common.dto.api.Pagination
import com.example.common.dto.api.ScrollPositionDto
import com.example.common.dto.business.RoomDto
import com.example.gamehandlerservice.model.dto.*
import com.example.gamehandlerservice.model.game.AfterDropCardResult
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.roomservice.repository.RoomEntity
import jakarta.validation.Validation.buildDefaultValidatorFactory
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DtoInitializationTests {
    private val validator: Validator

    init {
        val factory: ValidatorFactory = buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `AccountActionDTO should be valid with proper values`() {
        val dto = AccountActionDTO(AccountAction.LEAVE, 1L, "ActionName")

        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    @Test
    fun `RoomDTO should be valid with proper values`() {
        val dto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
        assertEquals("Комната для игры", dto.name)
        assertEquals(4,dto.capacity)
    }

    @Test
    fun `Pagination should be valid with proper values`() {
        val dto = Pagination(10,10)
        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
        assertEquals(10,dto.page)
        assertEquals(10,dto.pageSize)
    }

    @Test
    fun `ScrollPositionDto should be valid with proper values`() {
        val dto = ScrollPositionDto(10)
        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
        assertEquals(10, dto.offset)
    }

    @Test
    fun `AccountActionRequest should be valid with proper accountId`() {
        val request = AccountActionRequest(accountId = 1L)

        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }

    @Test
    fun `MessageDTO should be valid with proper test value`() {
        val dto = MessageDTO("Test message")

        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }


    @Test
    fun `MoveCardRequest should be valid with proper values`() {
        val card = Card(Suit.Hearts, 10, false)
        val request = MoveCardRequest(fromDropArea = 1L, toDropArea = 2L, card = card)
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }

    @Test
    fun `MoveCardResponse should initialize properly`() {
        val card = Card(Suit.Diamonds, 5, true)
        val response = MoveCardResponse(idFrom = 1L, idTo = 2L, card = card)
        assertEquals(1L, response.idFrom)
        assertEquals(2L, response.idTo)
        assertEquals(card, response.card)
    }

    @Test
    fun `AfterDropCardResult constants should have correct values`() {
        assertEquals(true, AfterDropCardResult.Constants.nextStage.nextStage)
        assertEquals(false, AfterDropCardResult.Constants.noStageChanges.nextStage)
    }
}