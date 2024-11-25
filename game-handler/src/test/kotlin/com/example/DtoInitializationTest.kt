package com.example

import com.example.common.dto.api.Pagination
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.dto.roomservice.AccountAction
import com.example.common.dto.roomservice.AddAccountRequest
import com.example.common.dto.roomservice.RemoveAccountRequest
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.model.dto.*
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.CardCompareResult
import com.example.gamehandlerservice.model.game.CardDropResult
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.game.NextRoundAction
import jakarta.validation.Validation.buildDefaultValidatorFactory
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DtoInitializationTest {
    private val validator: Validator

    init {
        val factory: ValidatorFactory = buildDefaultValidatorFactory()
        validator = factory.validator
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
        assertEquals(4, dto.capacity)
    }

    @Test
    fun `Pagination should be valid with proper values`() {
        val dto = Pagination(10, 10)
        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
        assertEquals(10, dto.page)
        assertEquals(10, dto.pageSize)
    }


    @Test
    fun `AccountDto should be valid with proper values`() {
        val dto = AccountDto(1L, "Test", 2, "avatar", 1L)
        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
        assertEquals(1L, dto.id)
        assertEquals("Test", dto.name)
        assertEquals(2, dto.fines)
        assertEquals(1L, dto.roomId)
    }


    @Test
    fun `AddAccountRequest constants should have correct values`() {
        val addAccReq = AddAccountRequest(1L);
        assertEquals(1L, addAccReq.accountId)
    }

    @Test
    fun `RemoveAccountRequest constants should have correct values`() {
        val addAccReq = RemoveAccountRequest(AccountAction.KICK);
        assertEquals(AccountAction.KICK, addAccReq.reason)
    }

    @Test
    fun `test CardDropResult initialization`() {
        val dropResult1 = CardDropResult(changeTurn = true, valid = false, needsFine = true)
        assertEquals(true, dropResult1.changeTurn)
        assertEquals(false, dropResult1.valid)
        assertEquals(true, dropResult1.needsFine)

        val dropResult2 = CardDropResult(changeTurn = true, valid = true, needsFine = false)
        assertEquals(true, dropResult2.changeTurn)
        assertEquals(true, dropResult2.valid)
        assertEquals(false, dropResult2.needsFine)

        val dropResult3 = CardDropResult(changeTurn = false, valid = false, needsFine = false)
        assertEquals(false, dropResult3.changeTurn)
        assertEquals(false, dropResult3.valid)
        assertEquals(false, dropResult3.needsFine)
    }

    @Test
    fun `test CardDropResult companion object constants`() {
        val invalidResult = CardDropResult.invalid
        assertEquals(true, invalidResult.changeTurn)
        assertEquals(false, invalidResult.valid)
        assertEquals(true, invalidResult.needsFine)

        val validResult = CardDropResult.valid
        assertEquals(true, validResult.changeTurn)
        assertEquals(true, validResult.valid)
        assertEquals(false, validResult.needsFine)

        val missClickResult = CardDropResult.missClick
        assertEquals(false, missClickResult.changeTurn)
        assertEquals(false, missClickResult.valid)
        assertEquals(false, missClickResult.needsFine)
    }


    @Test
    fun `RoomDTO constants should have correct values`() {
        val roomDto = RoomDto(
            id = 0,
            name = "Комната для игры",
            hostId = 1L,
            capacity = 4,
            currentGameId = 0L,
            players = mutableListOf(), bannedPlayers = mutableListOf()
        )
        assertEquals(1L, roomDto.hostId)
        assertEquals(4, roomDto.capacity)
        assertEquals("Комната для игры", roomDto.name)
        assertEquals(0L, roomDto.currentGameId)
    }

    @Test
    fun `UpdateAccountRoomRequest constants should have correct values`() {
        val updateAccountRoomRequest = UpdateAccountRoomRequest(
            roomId = 1L
        )
        assertEquals(1L, updateAccountRoomRequest.roomId)
    }

    @Test
    fun `test Stage enum values`() {
        val stages = GameStage.values()

        assertEquals(2, stages.size)
        assertTrue(stages.contains(GameStage.WAITING))
        assertTrue(stages.contains(GameStage.STARTED))
    }

    @Test
    fun `test CardCompareResult enum values`() {
        val cardCompareResult = CardCompareResult.values()

        assertEquals(4, cardCompareResult.size)
        assertTrue(cardCompareResult.contains(CardCompareResult.NOT_COMPARABLE))
        assertTrue(cardCompareResult.contains(CardCompareResult.LESS))
        assertTrue(cardCompareResult.contains(CardCompareResult.MORE))
        assertTrue(cardCompareResult.contains(CardCompareResult.EQUALS))

    }
    @Test
    fun `test NextRoundAction enum values`() {
        val nextRoundAction = NextRoundAction.values()

        assertEquals(3, nextRoundAction.size)
        assertTrue(nextRoundAction.contains(NextRoundAction.NEXT_ROUND))
        assertTrue(nextRoundAction.contains(NextRoundAction.SKIP_ROUND))
        assertTrue(nextRoundAction.contains(NextRoundAction.SWITCH_ROUND))

    }


    @Test
    fun `test PlayerAction enum values`() {
        val stages = PlayerAction.values()
        assertEquals(3, stages.size)
        assertTrue(stages.contains(PlayerAction.DROP_CARD))
        assertTrue(stages.contains(PlayerAction.BEAT))
        assertTrue(stages.contains(PlayerAction.TAKE))
    }

    @Test
    fun `GameState constants should have correct values`() {
        val gameState = GameState(
            attackPlayer = 1L,
            defendPlayer = 2L,
            isDefending = true
        )
        assertEquals(1L, gameState.attackPlayer)
        assertEquals(2L, gameState.defendPlayer)
        assertEquals(true, gameState.isDefending)

    }

    @Test
    fun `GameStateResponse should hold correct values`() {
        val card1 = Card(Suit.SPADES, 6)
        val card2 = Card(Suit.CLUBS, 5)
        val trumpCard = Card(Suit.HEARTS, 7)

        val players = listOf(
            AccountDto(1L, "Test", 2, "avatar", 1L),
            AccountDto(2L, "Test2", 2, "avatar", 1L)
        )

        val playersCardsCount = mapOf(
            1L to 5,
            2L to 3
        )

        val state = GameState(
            attackPlayer = 1L,
            defendPlayer = 2L,
            isDefending = true
        )

        val gameStateResponse = GameStateResponse(
            table = listOf(card1, card2),
            state = state,
            trumpCard = trumpCard,
            deckSize = 30,
            stage = GameStage.STARTED,
            winner = null,
            players = players,
            playersCardsCount = playersCardsCount,
            hostId = 1L
        )

        assertEquals(listOf(card1, card2), gameStateResponse.table)
        assertEquals(state, gameStateResponse.state)
        assertEquals(trumpCard, gameStateResponse.trumpCard)
        assertEquals(30, gameStateResponse.deckSize)
        assertEquals(GameStage.STARTED, gameStateResponse.stage)
        assertEquals(null, gameStateResponse.winner)
        assertEquals(players, gameStateResponse.players)
        assertEquals(playersCardsCount, gameStateResponse.playersCardsCount)
        assertEquals(1L, gameStateResponse.hostId)
    }

    @Test
    fun `PlayerActionRequest constants should have correct values`() {
        val playerActionRequest = PlayerActionRequest(
            playerId = 1L,
            droppedCard = Card(suit = Suit.SPADES),
            action = PlayerAction.BEAT
        )
        assertEquals(1L, playerActionRequest.playerId)
        assertEquals(Suit.SPADES, playerActionRequest.droppedCard!!.suit)
        assertEquals(PlayerAction.BEAT, playerActionRequest.action)

    }

    @Test
    fun `PlayerCardsEvent constants should have correct values`() {
        val playerCardsEvent = PlayerCardsEvent(
            cardsInHand = listOf(Card(suit = Suit.SPADES))
        )
        assertEquals(Suit.SPADES, playerCardsEvent.cardsInHand.get(0).suit)
    }
}