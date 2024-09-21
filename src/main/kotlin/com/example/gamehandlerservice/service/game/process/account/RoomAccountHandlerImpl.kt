package com.example.gamehandlerservice.service.game.process.account

import com.example.personalaccount.database.Account
import com.example.personalaccount.database.AccountRepo
import com.example.gamehandlerservice.model.dto.AccountAction
import com.example.gamehandlerservice.model.dto.AccountActionDTO
import com.example.gamehandlerservice.model.dto.FineDTO
import com.example.gamehandlerservice.model.dto.RoomAccountsOperationResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Scope
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import kotlin.properties.Delegates


@Component
@Scope("prototype")
class RoomAccountHandlerImpl(
    val accountRepo: AccountRepo,
    val simpMessagingTemplate: SimpMessagingTemplate
) : RoomAccountHandler {

    private var capacity by Delegates.notNull<Int>()
    private val players: MutableMap<Long, Account> = HashMap()
    private val roomOverflow = RoomAccountsOperationResult(false, "Room is full!")
    private val roomSuccess = RoomAccountsOperationResult(true, null)
    private val notFound = RoomAccountsOperationResult(false, "no such player")

    override fun configure(capacity: Int) {
        this.capacity = capacity
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendAccountAction(accountAction: AccountAction, account: Account) {
        GlobalScope.launch(Dispatchers.IO) {
            simpMessagingTemplate.convertAndSend("/topic/accounts", AccountActionDTO(accountAction, account.id, account.name))
        }
    }


    override fun addAccount(account: Account): RoomAccountsOperationResult {
        if (players.size >= capacity) {
            return roomOverflow
        }
        players[account.id] = account
        sendAccountAction(AccountAction.ADD, account)
        return roomSuccess
    }

    override fun kickAccount(id: Long): RoomAccountsOperationResult {
        return if(players.contains(id)) {
            sendAccountAction(AccountAction.KICK, players[id]!!)
            players.remove(id)
            roomSuccess
        } else {
            notFound
        }

    }

    override fun banAccount(id: Long): RoomAccountsOperationResult {
        return if(players.contains(id)) {
            val curPlayer = players[id]
            sendAccountAction(AccountAction.BAN,curPlayer!!)
            players.remove(id)
            accountRepo.delete(curPlayer)
            roomSuccess
        } else {
            notFound
        }
    }

}