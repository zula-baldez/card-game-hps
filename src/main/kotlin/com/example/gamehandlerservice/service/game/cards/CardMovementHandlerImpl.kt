package com.example.gamehandlerservice.service.game.cards

import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.dto.MoveCardResponse
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.util.VirtualPlayers
import com.example.personalaccount.database.Account
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
) : CardMovementHandler {
    override val cards: MutableMap<Long, LinkedHashSet<Card>> = HashMap()
    override lateinit var trump: Suit

    override fun moveCard(moveCardRequest: MoveCardRequest) {
        val sourceArea = cards[moveCardRequest.fromDropArea]
        val destinationArea = cards[moveCardRequest.toDropArea]

        sourceArea?.let { source ->
            if (destinationArea != null && source.remove(moveCardRequest.card)) {
                destinationArea.add(moveCardRequest.card)
                sendCardMoveToDest(moveCardRequest.fromDropArea, moveCardRequest.toDropArea, moveCardRequest.card)
            }
        }
    }

    override fun giveUsersBasicCards(players: List<Account>) {
        players.forEach { account -> cards[account.id] = LinkedHashSet() }
        cards[VirtualPlayers.TABLE.id] = LinkedHashSet()

        val deck = generateDeck().toMutableList()

        players.forEach { account ->
            repeat(account.additionalCards + 2) {
                deck.removeLastOrNull()?.let { card ->
                    card.secret = true
                    cards[account.id]?.add(card)
                    sendCardMoveToDest(VirtualPlayers.DECK.id, account.id, card)
                }
            }
            deck.removeLastOrNull()?.let { card ->
                cards[account.id]?.add(card)
                sendCardMoveToDest(VirtualPlayers.DECK.id, account.id, card)

            }
        }

        deck.forEach {
            cards[VirtualPlayers.TABLE.id]?.add(it)
            sendCardMoveToDest(VirtualPlayers.DECK.id, VirtualPlayers.TABLE.id, it)
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