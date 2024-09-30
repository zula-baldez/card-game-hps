package com.example.roomservice.service

import com.example.gamehandlerservice.model.dto.AccountAction
import com.example.gamehandlerservice.model.dto.AccountActionDTO
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.dto.RoomAccountActionResult
import com.example.roomservice.repository.RoomRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Scope
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
@Scope("prototype")
class RoomAccountManagerImpl(
    val roomRepository: RoomRepository,
    val accountRepository: AccountRepository,
    val simpMessagingTemplate: SimpMessagingTemplate
) : RoomAccountManager {

    private val roomSuccess = RoomAccountActionResult(true, null)
    private val roomOverflow = RoomAccountActionResult(false, "Room is full!")
    private val playerNotFound = RoomAccountActionResult(false, "no such player")
    private val roomNotFound = RoomAccountActionResult(false, "no such room")

    override fun addAccount(roomId: Long, accountId: Long): RoomAccountActionResult {
        val room = roomRepository.findById(roomId).getOrNull() ?: return roomNotFound
        val account = accountRepository.findById(accountId).getOrNull() ?: return playerNotFound
        return if (room.players.size >= room.capacity)
            roomOverflow
        else {
            account.roomEntity = room
            room.players.addLast(account)
            roomRepository.save(room)
            accountRepository.save(account)
            roomSuccess
        }
    }

    override fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction): RoomAccountActionResult {
        val room = roomRepository.findById(roomId).getOrNull() ?: return roomNotFound
        val account = accountRepository.findById(accountId).getOrNull() ?: return playerNotFound

        return if (room.players.contains(account)) {
            if (reason == AccountAction.BAN) {
                room.bannedPlayers += account
            }
            sendAccountAction(reason, account)
            room.players.remove(account)
            if (room.players.isEmpty())
                roomRepository.deleteById(roomId)
            roomSuccess
        } else playerNotFound
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendAccountAction(accountAction: AccountAction, accountEntity: AccountEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            simpMessagingTemplate.convertAndSend(
                "/topic/accounts",
                AccountActionDTO(accountAction, accountEntity.id, accountEntity.name)
            )
        }
    }
}