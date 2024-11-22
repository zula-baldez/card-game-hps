package com.example.gamehandlerservice.service.game.game

import com.example.common.client.RoomServiceClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.kafkaconnections.ConnectionMessage
import com.example.common.kafkaconnections.ConnectionMessageType
import com.example.common.kafkaconnections.KafkaConnectionsSender
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
    private val roomServiceClient: RoomServiceClient,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val sender: KafkaConnectionsSender
) : GameHandler {

    private val playersCards: MutableMap<Long, MutableList<Card>> = mutableMapOf()
    private var players: List<AccountDto> = mutableListOf()
    private var lastRoundWinner: Long? = null
    private var trumpCard: Card? = null
    private val deck: MutableList<Card> = mutableListOf()
    private val table: MutableList<Card> = mutableListOf()
    private var stage: GameStage = GameStage.WAITING
    private var queue: CyclicQueue<Long> = CyclicQueue(listOf())
    private var state: GameState = GameState(0, 0, isDefending = false)
    private var roomId: Long = 0

    override fun setRoomId(roomId: Long) {
        this.roomId = roomId
    }

    override fun playerDisconnect(accountId: Long): AccountDto {
        val player = kickPlayer(accountId)
        sender.send(
            "game-connection-to-room-service",
            ConnectionMessage(
                ConnectionMessageType.DISCONNECT,
                roomId,
                player
            )
        )
        return player
    }

    override fun kickPlayer(accountId: Long): AccountDto {
        queue.delete(accountId)
        val player = players.find { it.id == accountId } ?: throw IllegalArgumentException()
        players -= player
        sendGameState()
        return player
    }

    override fun addPlayer(account: AccountDto) {
        players += account
        sendGameState()
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

        val room = roomServiceClient.findById(roomId)
        val players = room.players.map { it.id }
        trumpCard = deck.first()
        for (player in room.players.map { it.id }) {
            playersCards[player] = mutableListOf()
        }
        fillPlayersCards()
        for (player in playersCards.keys) {
            sendPlayerCards(player)
        }
        queue = CyclicQueue(players.shuffled())
        newRound()
        stage = GameStage.STARTED

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
            players = players
        )
    }
}
