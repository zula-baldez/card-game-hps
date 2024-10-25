package com.example.gamehandlerservice.service.game.cards

import com.example.common.dto.personalaccout.business.AccountDto
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.dto.MoveCardResponse
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.game.GameHandler
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Scope
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class CardMovementHandlerImpl(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val objectMapper: ObjectMapper
) : CardMovementHandler {

    override fun moveCard(moveCardRequest: MoveCardRequest, gameHandler: GameHandler) {
        val sourceArea = gameHandler.gameData.userCards[moveCardRequest.fromDropArea]
        val destinationArea = gameHandler.gameData.userCards[moveCardRequest.toDropArea]

        sourceArea?.let { source ->
            if (destinationArea != null && source.remove(moveCardRequest.card)) {
                destinationArea.add(moveCardRequest.card)
                sendCardMoveToDest(moveCardRequest.fromDropArea, moveCardRequest.toDropArea, moveCardRequest.card)
            }
        }
    }

    override fun giveUsersBasicCards(players: List<AccountDto>, gameHandler: GameHandler) {
        players.forEach { account -> gameHandler.gameData.userCards[account.id] = LinkedHashSet() }
        gameHandler.gameData.userCards[VirtualPlayers.TABLE.id] = LinkedHashSet()

        val deck = generateDeck().toMutableList()

        players.forEach { account ->
            repeat(account.fines + 2) {
                deck.removeLastOrNull()?.let { card ->
                    card.secret = true
                    gameHandler.gameData.userCards[account.id]?.add(card)
                    sendCardMoveToDest(VirtualPlayers.DECK.id, account.id, card)
                }
            }
            deck.removeLastOrNull()?.let { card ->
                gameHandler.gameData.userCards[account.id]?.add(card)
                sendCardMoveToDest(VirtualPlayers.DECK.id, account.id, card)
            }
        }

        deck.forEach {
            gameHandler.gameData.userCards[VirtualPlayers.TABLE.id]?.add(it)
            sendCardMoveToDest(VirtualPlayers.DECK.id, VirtualPlayers.TABLE.id, it)
        }
    }

    override fun clearPlayerCards(playerId: Long, gameHandler: GameHandler) {
        gameHandler.gameData.userCards[playerId]?.forEach { card ->
            moveCard(MoveCardRequest(playerId, VirtualPlayers.DECK.id, card), gameHandler)
        }
    }

    private fun generateDeck(): MutableList<Card> {
        return Suit.values().flatMap { suit ->
            (2L..14L).map { strength -> Card(suit, strength, false) }
        }.toMutableList()
    }

    private fun sendCardMoveToDest(idFrom: Long?, idTo: Long, card: Card) {

        CoroutineScope(Dispatchers.IO).launch {
            simpMessagingTemplate.convertAndSend("/topic/card-changes", MoveCardResponse(idFrom, idTo, card))
        }
    }
}