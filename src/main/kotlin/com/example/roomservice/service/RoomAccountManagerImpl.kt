package com.example.roomservice.service

import com.example.common.exceptions.AccountNotFoundException
import com.example.common.exceptions.RoomNotFoundException
import com.example.common.exceptions.RoomOverflowException
import com.example.gamehandlerservice.model.dto.AccountAction
import com.example.gamehandlerservice.model.dto.AccountActionDTO
import com.example.personalaccount.database.AccountEntity
import com.example.personalaccount.database.AccountRepository
import com.example.roomservice.repository.RoomRepository
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

    override fun addAccount(roomId: Long, accountId: Long) {
        val room = roomRepository.findById(roomId).getOrNull() ?: throw RoomNotFoundException(roomId)
        val account = accountRepository.findById(accountId).getOrNull() ?: throw AccountNotFoundException(accountId)
        if (room.players.size >= room.capacity)
            throw RoomOverflowException(roomId)
        else {
            account.roomEntity = room
            room.players.addLast(account)
            roomRepository.save(room)
            accountRepository.save(account)
        }
    }

    override fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction) {
        val room = roomRepository.findById(roomId).getOrNull() ?: throw RoomNotFoundException(roomId)
        val account = accountRepository.findById(accountId).getOrNull() ?: throw AccountNotFoundException(accountId)

        if (room.players.contains(account)) {
            if (reason == AccountAction.BAN) {
                room.bannedPlayers += account
            }
            sendAccountAction(reason, account)
            room.players.remove(account)
            if (room.players.isEmpty()) {
                roomRepository.deleteById(roomId)
            }
            roomRepository.save(room)
        } else {
            throw AccountNotFoundException(accountId)
        }
    }

    private fun sendAccountAction(accountAction: AccountAction, accountEntity: AccountEntity) {
        simpMessagingTemplate.convertAndSend(
            "/topic/accounts",
            AccountActionDTO(accountAction, accountEntity.id, accountEntity.name)
        )
    }
}