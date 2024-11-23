package com.example.roomservice.service

import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.dto.roomservice.AccountAction
import com.example.common.exceptions.AccountNotFoundException
import com.example.common.exceptions.ForbiddenOperationException
import com.example.common.exceptions.RoomNotFoundException
import com.example.common.exceptions.RoomOverflowException
import com.example.common.kafkaconnections.RoomUpdateEvent
import com.example.common.kafkaconnections.RoomUpdateEvent.Companion.PlayerLeaveEvent
import com.example.common.kafkaconnections.RoomUpdateEvent.Companion.RoomUpdateEventType
import com.example.roomservice.repository.AccountInRoomEntity
import com.example.roomservice.repository.AccountInRoomRepository
import com.example.roomservice.repository.BannedAccountInRoomEntity
import com.example.roomservice.repository.BannedAccountInRoomRepository
import com.example.roomservice.repository.RoomEntity
import com.example.roomservice.repository.RoomRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
@Scope("prototype")
class RoomAccountManagerImpl(
    val roomRepository: RoomRepository,
    val accountInRoomRepository: AccountInRoomRepository,
    val bannedAccountInRoomRepository: BannedAccountInRoomRepository,
    val personalAccountClient: ReactivePersonalAccountClient,
    val sender: RoomUpdateEventSender
) : RoomAccountManager {
    private val logger = LoggerFactory.getLogger(RoomAccountManagerImpl::class.java)

    @Transactional
    override fun addAccount(roomId: Long, accountId: Long): Mono<Void> {
        return roomRepository
            .findById(roomId)
            .switchIfEmpty(Mono.error { RoomNotFoundException(roomId) })
            .flatMap { room ->
                return@flatMap getBannedAccountsInRoom(room.id)
                    .collectList()
                    .handle { bannedAccounts, sink ->
                        if (bannedAccounts.contains(accountId)) {
                            sink.error(ForbiddenOperationException())
                        } else {
                            sink.next(room)
                        }
                    }
            }
            .flatMap { room ->
                return@flatMap getAccountsInRoom(room.id)
                    .collectList()
                    .handle { accounts, sink ->
                        if (accounts.contains(accountId)) {
                            sink.error(ForbiddenOperationException())
                        } else if (accounts.size >= room.capacity) {
                            sink.error(RoomOverflowException(roomId))
                        } else {
                            sink.next(room)
                        }
                    }
            }
            .flatMap { room ->
                return@flatMap getAccountRoom(accountId)
                    .flatMap { Mono.error<RoomEntity>(ForbiddenOperationException()) }
                    .switchIfEmpty(Mono.just(room))
            }
            .flatMap { room ->
                return@flatMap accountInRoomRepository.save(AccountInRoomEntity(accountId, roomId, isNewAccount = true))
                    .flatMap { updateAccountRoom(accountId, roomId) }
                    .flatMap { account ->
                        sendPlayerRoomUpdate(account, roomId, room.hostId, false)
                    }
            }
    }

    override fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction): Mono<Void> {
        return roomRepository
            .findById(roomId)
            .switchIfEmpty(Mono.error(RoomNotFoundException(roomId)))
            .flatMap { room ->
                return@flatMap accountInRoomRepository.findAllByRoomId(room.id)
                    .collectList()
                    .flatMap {
                        handlePlayerLeave(room, it, accountId, reason)
                    }
            }
    }

    private fun handlePlayerLeave(room: RoomEntity, playersInRoom: MutableList<AccountInRoomEntity>, accountId: Long, reason: AccountAction): Mono<Void> {
        val accountToRemove = playersInRoom.find { it.accountId == accountId }
        var hostId = room.hostId
        if (accountToRemove == null) {
            return Mono.error(AccountNotFoundException(accountId))
        }

        if (playersInRoom.size <= 1) {
            return roomRepository.deleteById(room.id)
                .then(updateAccountRoom(accountId, null))
                .flatMap {
                    Mono.fromRunnable {
                        sender.sendRoomUpdateEvent(
                            RoomUpdateEvent(
                                room.id, RoomUpdateEventType.ROOM_DELETED
                            )
                        )
                    }
                }
        } else {
            return accountInRoomRepository.delete(accountToRemove)
                .then(
                    if (accountId == hostId) {
                        playersInRoom.remove(accountToRemove)
                        hostId = playersInRoom.first().accountId
                        roomRepository.save(room.copy(hostId = hostId)).then()
                    } else {
                        Mono.empty()
                    }
                )
                .then(
                    if (reason == AccountAction.BAN) {
                        bannedAccountInRoomRepository.save(
                            BannedAccountInRoomEntity(
                                0,
                                accountId,
                                room.id
                            )
                        )
                    } else {
                        Mono.empty()
                    }
                )
                .then(
                    updateAccountRoom(accountId, null).flatMap {
                        sendPlayerRoomUpdate(it, room.id, hostId, isLeave = true)
                    }
                )
        }
    }

    private fun updateAccountRoom(accountId: Long, roomId: Long?): Mono<AccountDto> {
        return personalAccountClient.updateAccountRoom(accountId, UpdateAccountRoomRequest(roomId))
    }

    private fun sendPlayerRoomUpdate(account: AccountDto, roomId: Long, roomHost: Long, isLeave: Boolean): Mono<Void> {
        return Mono.fromRunnable {
            if (isLeave) {
                sender.sendRoomUpdateEvent(
                    RoomUpdateEvent(
                        roomId,
                        RoomUpdateEventType.PLAYER_LEAVE,
                        playerLeave = PlayerLeaveEvent(
                            accountId = account.id,
                            newHost = roomHost
                        )
                    )
                )
            } else {
                sender.sendRoomUpdateEvent(
                    RoomUpdateEvent(
                        roomId,
                        RoomUpdateEventType.PLAYER_JOIN,
                        newPlayer = account
                    )
                )
            }
        }
    }

    override fun getAccountRoom(accountId: Long): Mono<Long> {
        return accountInRoomRepository.findById(accountId).map { it.roomId }
    }

    override fun getAccountsInRoom(roomId: Long): Flux<Long> {
        return accountInRoomRepository.findAllByRoomId(roomId).map { it.accountId }
    }

    override fun getBannedAccountsInRoom(roomId: Long): Flux<Long> {
        return bannedAccountInRoomRepository.findAllByRoomId(roomId).map { it.accountId }
    }
}