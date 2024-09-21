package com.example.gamehandlerservice.service.game.process.cards

import com.example.personalaccount.database.Account
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.dto.MoveCardResponse
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.Suit
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Scope
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class CardHandlerImpl(
    private val simpMessagingTemplate: SimpMessagingTemplate
) : CardHandler {
    override val cards: MutableMap<Long, LinkedHashSet<Card>> = HashMap()
    override lateinit var trump: Suit

    override fun moveCard(moveCardRequest: MoveCardRequest) {
        val sourceArea = cards[moveCardRequest.fromDropArea]
        val destinationArea = cards[moveCardRequest.toDropArea]

        if (sourceArea != null && destinationArea != null && sourceArea.contains(moveCardRequest.card)) {
            sourceArea.remove(moveCardRequest.card)
            destinationArea.add(moveCardRequest.card)
            sendCardMoveToDest(moveCardRequest.fromDropArea, moveCardRequest.toDropArea, moveCardRequest.card)
        }
    }

    override fun giveUsersBasicCards(players: List<Account>) {
        players.forEach { account -> cards[account.id] = LinkedHashSet() }
        cards[-1] = LinkedHashSet()
        val deck = generateDeck().toMutableList()
        players.forEach { account ->
            for (i in 1..(account.additionalCards + 2)) {
                val lastCard = deck.removeLast()
                lastCard.secret = true
                cards[account.id]?.add(lastCard)
                sendCardMoveToDest(-2, account.id, lastCard)
            }
            val lastCard = deck.removeLast()
            cards[account.id]?.add(lastCard)
            sendCardMoveToDest(-2, account.id, lastCard)
        }

        deck.forEach {
            cards[-1]?.add(it)
            sendCardMoveToDest(-2, -1, it)
        }
    }

    private fun generateDeck(): MutableList<Card> {
        val cards: MutableList<Card> = ArrayList()
        Suit.values().forEach { suit ->
            for (strength: Long in 2L..14L) {
                val card = Card(suit, strength, false)
                cards.add(card)
            }
        }
        return cards
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendCardMoveToDest(idFrom: Long?, idTo: Long, card: Card) {
        GlobalScope.launch(Dispatchers.IO) {
            simpMessagingTemplate.convertAndSend("/topic/card-changes", MoveCardResponse(idFrom, idTo, card))
        }
    }
}