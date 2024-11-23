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
import com.example.common.kafkaconnections.RoomUpdateEvent.Companion.RoomUpdateEventType
import com.example.common.kafkaconnections.RoomUpdateEvent.Companion.PlayerLeaveEvent
import com.example.roomservice.repository.*
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
@Scope("prototype")
class RoomAccountManagerImpl(
    val roomManager: RoomManager,
    val roomRepository: RoomRepository,
    val accountInRoomRepository: AccountInRoomRepository,
    val bannedAccountInRoomRepository: BannedAccountInRoomRepository,
    val personalAccountClient: ReactivePersonalAccountClient,
    val sender: RoomUpdateEventSender
) : RoomAccountManager {
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
                    .then()
            }

    }

    override fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction): Mono<Void> {
        return roomRepository
            .findById(roomId)
            .switchIfEmpty(Mono.error(RoomNotFoundException(roomId)))
            .flatMap { room ->
                return@flatMap accountInRoomRepository.findAllByRoomId(room.id)
                    .collectList()
                    .flatMap withAccounts@{ accounts ->
                        val accountToRemove = accounts.find { it.accountId == accountId }
                        var hostId = room.hostId
                        if (accountToRemove == null) {
                            return@withAccounts Mono.error(AccountNotFoundException(accountId))
                        }

                        if (accounts.size <= 1) {
                            return@withAccounts roomManager.deleteRoom(roomId)
                                .then(updateAccountRoom(accountId, null))
                                .then(Mono.empty())
                        } else {
                            return@withAccounts accountInRoomRepository.delete(accountToRemove)
                                .then(
                                    if (accountId == hostId) {
                                        accounts.remove(accountToRemove)
                                        hostId = accounts.first().accountId
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
                                                roomId
                                            )
                                        ).then()
                                    } else {
                                        Mono.empty()
                                    }
                                )
                                .then(updateAccountRoom(accountId, null).map { account ->
                                    sendPlayerRoomUpdate(account, roomId, hostId, isLeave = true)
                                })
                                .then()
                        }
                    }
            }
    }

    private fun updateAccountRoom(accountId: Long, roomId: Long?): Mono<AccountDto> {
        return personalAccountClient.updateAccountRoom(accountId, UpdateAccountRoomRequest(roomId))
    }

    private fun sendPlayerRoomUpdate(account: AccountDto, roomId: Long, roomHost: Long, isLeave: Boolean) {
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