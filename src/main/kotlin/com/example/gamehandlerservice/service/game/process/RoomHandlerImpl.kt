package com.example.gamehandlerservice.service.game.process

import com.example.personalaccount.database.Account
import com.example.gamehandlerservice.model.dto.MoveCardRequest
import com.example.gamehandlerservice.model.dto.RoomAccountsOperationResult
import com.example.gamehandlerservice.model.game.Stage
import com.example.gamehandlerservice.service.game.process.account.RoomAccountHandler
import com.example.gamehandlerservice.service.game.process.cards.CardHandler
import com.example.gamehandlerservice.service.game.process.drop.DropStrategy
import com.example.personalaccount.service.PersonalAccountManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
@Scope("prototype")
class RoomHandlerImpl(
    val roomAccountHandler: RoomAccountHandler,
    val cardHandler: CardHandler,
    val friendsManager: PersonalAccountManager,
    dropHandlersList: List<DropStrategy>,
) : RoomHandler {
    private val dropHandlers: Map<Stage, DropStrategy> = dropHandlersList.associateBy { it.stage }
    private lateinit var stage: Stage
    override var id by Delegates.notNull<Long>()
    override var hostId by Delegates.notNull<Long>()
    var turnIdIndex = -1
    private var timer: Job? = null
    private lateinit var playerIds: MutableList<Long>
    override lateinit var name: String
    override val count: Long
        get() = friendsManager.getInRoomAccounts().size.toLong()
    override var capacity by Delegates.notNull<Int>()

    override fun configureGameHandler(name: String, stage: Stage, id: Long, hostId: Long, capacity: Int) {
        this.stage = stage
        this.id = id
        this.hostId = hostId
        this.roomAccountHandler.configure(capacity)
        this.name = name
        this.capacity = capacity
    }

    override fun turningPlayerId(): Long = playerIds[turnIdIndex]

    override fun getAllPlayers(): List<Account> = friendsManager.getInRoomAccounts() //TODO CRINGE

    override fun addAccount(account: Account): RoomAccountsOperationResult = roomAccountHandler.addAccount(account)

    override fun kickAccount(id: Long): RoomAccountsOperationResult = roomAccountHandler.kickAccount(id)

    override fun banAccount(id: Long): RoomAccountsOperationResult = roomAccountHandler.banAccount(id)

    override fun moveCard(moveCardRequest: MoveCardRequest) {
        restartTimer() //todo aspect
        val strategy: DropStrategy = dropHandlers[stage]!!
        val result = strategy.validateDrop(playerIds[turnIdIndex], moveCardRequest, cardHandler.cards, this)
        if (result.needsFine) {
            friendsManager.addFine(playerIds[turnIdIndex])
        }
        if (result.changeTurn) {
            changeTurn()
        }
        if (result.valid) {
            cardHandler.moveCard(moveCardRequest)
        }
        if (result.nextStage?.equals(stage))
    }

    override fun startGame() {
        stage = Stage.DISTRIBUTION
        playerIds = friendsManager.getInRoomAccounts().map { it.id }.toMutableList()
        playerIds.shuffle()
        cardHandler.giveUsersBasicCards(roomAccountHandler.getAccounts())
        changeTurn()
        restartTimer()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun restartTimer() {
        timer?.cancel()
        timer = GlobalScope.launch(Dispatchers.IO) {
            startTimer()
        }
    }

    suspend fun startTimer() {
        delay(30000)
        timeOver()
    }

    private fun timeOver() {
        changeTurn()
        restartTimer()
    }

    private fun changeTurn() {
        turnIdIndex++
        if (turnIdIndex >= playerIds.size) {
            turnIdIndex = 0
        }
    }
}
