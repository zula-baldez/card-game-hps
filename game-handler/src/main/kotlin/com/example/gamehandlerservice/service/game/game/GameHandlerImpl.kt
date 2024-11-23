package com.example.gamehandlerservice.service.game.game

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import com.example.gamehandlerservice.exceptions.GameException
import com.example.gamehandlerservice.model.dto.*
import com.example.gamehandlerservice.model.game.Card
import com.example.gamehandlerservice.model.game.CardCompareResult
import com.example.gamehandlerservice.model.game.Suit
import com.example.gamehandlerservice.service.game.util.CyclicQueue
import org.springframework.context.annotation.Scope
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class GameHandlerImpl(
    private val simpMessagingTemplate: SimpMessagingTemplate,
) : GameHandler {


    private var lastRoundWinner: Long? = null

    private var trumpCard: Card? = null
    private val deck: MutableList<Card> = mutableListOf()
    private val table: MutableList<Card> = mutableListOf()

    private var stage: GameStage = GameStage.WAITING
    private var state: GameState = GameState(0, 0, isDefending = false)

    private var roomId: Long = 0
    private var roomName: String = ""
    private var roomHost: Long = 0

    private val playersCards: MutableMap<Long, MutableList<Card>> = mutableMapOf() // only active players
    private var players: MutableList<AccountDto> = mutableListOf() // all players, including spectators
    private var queue: CyclicQueue<Long> = CyclicQueue(listOf()) // only active players who are still playing


    override fun setRoom(room: RoomDto) {
        roomId = room.id
        roomName = room.name
        roomHost = room.hostId
    }

    override fun updateHostId(newHostId: Long) {
        roomHost = newHostId
        sendGameState()
    }

    override fun addPlayer(account: AccountDto) {
        players += account
        sendGameState()
    }

    override fun removePlayer(accountId: Long) {
        players.removeIf { it.id == accountId }
        playersCards -= accountId
        queue.delete(accountId)

        if (accountId == state.defendPlayer || accountId == state.attackPlayer) {
            table.clear()
            // Step back so next round will not skip a player
            if (accountId == state.defendPlayer) {
                queue.move(-1)
            }
            handleNextRoundAction(NextRoundAction.NEXT_ROUND) // also sends new game state
        } else {
            sendGameState()
        }
    }

    private fun fillDeck() {
        deck.clear()
        Suit.values().forEach { suit ->
            for (i in 6..14) {
                deck.add(Card(suit, i))
            }
        }
        deck.shuffle()
    }

    private fun fillPlayersCards() {
        for (player in playersCards.keys) {
            while (playersCards[player]!!.size < 6 && deck.isNotEmpty()) {
                playersCards[player]!!.add(deck.removeLast())
            }
        }
    }

    private fun newRound() {
        state = GameState(queue.current(), queue.next(), isDefending = false)
    }

    private fun switchRound() {
        state = state.copy(isDefending = !state.isDefending)
    }

    override fun startGame() {
        if (stage == GameStage.STARTED) {
            throw GameException("Game already started")
        }

        table.clear()
        fillDeck()

        players.shuffle()
        val players = players.map { it.id }
        trumpCard = deck.first()
        for (player in players) {
            playersCards[player] = mutableListOf()
        }
        fillPlayersCards()
        for (player in playersCards.keys) {
            sendPlayerCards(player)
        }
        queue = CyclicQueue(players)
        newRound()
        stage = GameStage.STARTED

        sendGameState()
    }

    private fun handleNextRoundAction(nextRoundAction: NextRoundAction) {
        if (nextRoundAction != NextRoundAction.SWITCH_ROUND) {
            fillPlayersCards()
            sendPlayerCards(state.attackPlayer)
            sendPlayerCards(state.defendPlayer)

            for (player in playersCards.keys) {
                if (playersCards[player]!!.isEmpty()) {
                    queue.delete(player)
                }
            }

            if (queue.getSize() <= 1) {
                lastRoundWinner = queue.current()
                stage = GameStage.WAITING
            } else {
                if (nextRoundAction == NextRoundAction.NEXT_ROUND) {
                    queue.move(1)
                } else if (nextRoundAction == NextRoundAction.SKIP_ROUND) {
                    queue.move(2)
                }
                newRound()
            }
        } else {
            switchRound()
        }

        sendGameState()
    }

    override fun handle(playerAction: PlayerActionRequest) {
        if (stage != GameStage.STARTED) {
            throw GameException("Game hasn't started")
        }

        val nextRoundAction = if (!state.isDefending && playerAction.playerId == state.attackPlayer) {
            handleAttack(playerAction)
        } else if (state.isDefending && playerAction.playerId == state.defendPlayer) {
            handleDefense(playerAction)
        } else {
            throw GameException("Not your turn yet!")
        }

        handleNextRoundAction(nextRoundAction)
    }

    private fun takePlayerCardIfPossible(player: Long, card: Card) {
        if (!playersCards[player]?.contains(card)!!) {
            throw GameException("No card in hand!")
        }
        playersCards[player]?.remove(card)
    }

    private fun handleDefense(action: PlayerActionRequest): NextRoundAction {
        return when (action.action) {
            PlayerAction.DROP_CARD -> {
                if (action.droppedCard == null) {
                    throw GameException("No card in request")
                }

                val topCardOnTable = table.last()

                if (action.droppedCard.compareTo(trumpCard!!.suit, topCardOnTable) != CardCompareResult.MORE) {
                    throw GameException("Card is not more than top card")
                }

                takePlayerCardIfPossible(action.playerId, action.droppedCard)
                table.add(action.droppedCard)
                sendPlayerCards(action.playerId)

                if (playersCards[action.playerId]?.isEmpty() == true) {
                    NextRoundAction.NEXT_ROUND
                } else {
                    NextRoundAction.SWITCH_ROUND
                }
            }

            PlayerAction.TAKE -> {
                playersCards[action.playerId]!!.addAll(table)
                table.clear()
                sendPlayerCards(action.playerId)
                NextRoundAction.SKIP_ROUND
            }

            PlayerAction.BEAT -> {
                throw GameException("Invalid move when defending")
            }
        }
    }

    private fun handleAttack(action: PlayerActionRequest): NextRoundAction {
        return when (action.action) {
            PlayerAction.DROP_CARD -> {
                if (action.droppedCard == null) {
                    throw GameException("No card in request")
                }

                val strengthsOnTable = table.map { it.strength }.toSet()

                if (strengthsOnTable.isNotEmpty() && !strengthsOnTable.contains(action.droppedCard.strength)) {
                    throw GameException("Invalid card dropped by attacker")
                }

                takePlayerCardIfPossible(action.playerId, action.droppedCard)
                table.add(action.droppedCard)
                sendPlayerCards(action.playerId)

                NextRoundAction.SWITCH_ROUND
            }

            PlayerAction.BEAT -> {
                table.clear()
                NextRoundAction.NEXT_ROUND
            }

            PlayerAction.TAKE -> {
                throw GameException("Invalid move when attacking")
            }
        }
    }

    private fun sendPlayerCards(playerId: Long) {
        // Skip if player is not active anymore
        if (!playersCards.keys.contains(playerId)) {
            return
        }

        simpMessagingTemplate.convertAndSend(
            "/topic/room/$roomId/players/$playerId/events",
            PlayerCardsEvent(cardsInHand = playersCards[playerId]!!)
        )
    }

    private fun sendGameState() {
        simpMessagingTemplate.convertAndSend(
            "/topic/room/$roomId/events",
            getGameState()
        )
    }

    override fun getGameState(): GameStateResponse {
        return GameStateResponse(
            table = table,
            state = state,
            trumpCard = trumpCard,
            deckSize = deck.size,
            stage = stage,
            winner = lastRoundWinner,
            players = players,
            playersCardsCount = playersCards.mapValues { it.value.size },
            hostId = roomHost
        )
    }
}
