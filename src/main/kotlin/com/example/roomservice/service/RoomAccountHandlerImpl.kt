package com.example.roomservice.service

import com.example.gamehandlerservice.model.dto.AccountAction
import com.example.gamehandlerservice.model.dto.AccountActionDTO
import com.example.gamehandlerservice.model.dto.RoomAccountsOperationResult
import com.example.personalaccount.database.Account
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Scope
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class RoomAccountHandlerImpl(
    val roomManager: RoomManager,
    val simpMessagingTemplate: SimpMessagingTemplate
) : RoomAccountHandler {

    private val roomOverflow = RoomAccountsOperationResult(false, "Room is full!")
    private val roomSuccess = RoomAccountsOperationResult(true, null)
    private val notFound = RoomAccountsOperationResult(false, "no such player")

    override fun addAccount(roomId: Long, account: Account): RoomAccountsOperationResult {
        val room = roomManager.getRoom(roomId)

        if (room != null && room.players.size >= room.capacity) {
            return roomOverflow
        }
        room?.players?.plus(account)
        if (room != null) {
            account.room = room
        }
        return roomSuccess
    }

    override fun kickAccount(roomId: Long, account: Account): RoomAccountsOperationResult {
        val room = roomManager.getRoom(roomId)
        return if (room?.players?.contains(account) == true) {
            sendAccountAction(AccountAction.KICK, account)
            room.players.minus(account)
            roomSuccess
        } else {
            notFound
        }
    }

    override fun banAccount(roomId: Long, account: Account): RoomAccountsOperationResult {
        val room = roomManager.getRoom(roomId)
        return if (room?.players?.contains(account) == true) {
            sendAccountAction(AccountAction.BAN, account)
            room.players.minus(room)
            roomSuccess
        } else {
            notFound
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendAccountAction(accountAction: AccountAction, account: Account) {
        GlobalScope.launch(Dispatchers.IO) {
            simpMessagingTemplate.convertAndSend(
                "/topic/accounts",
                AccountActionDTO(accountAction, account.id, account.name)
            )
        }
    }
}