package com.example.roomservice.service

import com.example.common.client.ReactivePersonalAccountClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import com.example.common.dto.roomservice.AccountAction
import com.example.common.exceptions.AccountNotFoundException
import com.example.common.exceptions.ForbiddenOperationException
import com.example.common.exceptions.RoomNotFoundException
import com.example.common.exceptions.RoomOverflowException
import com.example.common.kafkaconnections.KafkaConnectionsSender
import com.example.common.kafkaconnections.ConnectionMessage
import com.example.common.kafkaconnections.ConnectionMessageType
import com.example.roomservice.repository.*
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
    val sender: KafkaConnectionsSender
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
                    .flatMap {
                        updateGameHandler(it)
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
                    .flatMap withAccounts@{ accounts ->
                        val accountToRemove = accounts.find { it.accountId == accountId }
                        if (accountToRemove == null) {
                            return@withAccounts Mono.error(AccountNotFoundException(accountId))
                        }

                        if (accounts.size <= 1) {
                            return@withAccounts roomRepository.delete(room)
                                .then(updateAccountRoom(accountId, null))
                                .then(Mono.empty())
                        } else {
                            return@withAccounts accountInRoomRepository.delete(accountToRemove)
                                .then(
                                    if (accountId == room.hostId) {
                                        accounts.remove(accountToRemove)
                                        roomRepository.save(room.copy(hostId = accounts.first().accountId)).then()
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
                                .then(updateAccountRoom(accountId, null))
                                .then()
                        }
                    }
            }
    }

    private fun updateGameHandler(accountDto: AccountDto): Mono<Void> {
        sender.send(
            "game-connection-to-game-handler",
            ConnectionMessage(
                ConnectionMessageType.CONNECT,
                accountDto.roomId!!,
                accountDto
            )
        )
        return Mono.empty()
    }


    private fun updateAccountRoom(accountId: Long, roomId: Long?): Mono<AccountDto> {
        return personalAccountClient.updateAccountRoom(accountId, UpdateAccountRoomRequest(roomId))
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