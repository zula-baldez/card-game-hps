package com.example.roomservice.service

import com.example.common.dto.business.AccountAction
import com.example.common.exceptions.*
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
    val bannedAccountInRoomRepository: BannedAccountInRoomRepository
) : RoomAccountManager {
    @Transactional
    override fun addAccount(roomId: Long, accountId: Long, requesterId: Long): Mono<Void> {
        if (accountId != requesterId) {
            return Mono.error(ForbiddenOperationException())
        }

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
                return@flatMap accountInRoomRepository.save(AccountInRoomEntity(accountId, roomId, isNewAccount = true)).then(Mono.empty())
            }
    }

    override fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction, requesterId: Long): Mono<Void> {
        return roomRepository
            .findById(roomId)
            .switchIfEmpty(Mono.error(RoomNotFoundException(roomId)))
            .flatMap { room ->
                if (!(room.hostId == requesterId || requesterId == accountId)) {
                    return@flatMap Mono.error<Void> { HostOnlyException() }
                }

                return@flatMap accountInRoomRepository.findAllByRoomId(room.id)
                    .collectList()
                    .flatMap { accounts ->
                        val accountToRemove = accounts.find { it.accountId == accountId } ?: return@flatMap Mono.error<Void>(AccountNotFoundException(accountId))

                        if (accounts.size <= 1) {
                            return@flatMap roomRepository.delete(room)
                        } else {
                            return@flatMap accountInRoomRepository.delete(accountToRemove)
                                .and(
                                    if (accountId == room.hostId) {
                                        accounts.remove(accountToRemove)
                                        roomRepository.save(room.copy(hostId = accounts.first().accountId))
                                    } else {
                                        Mono.empty()
                                    }
                                )
                                .and(
                                    if (reason == AccountAction.BAN) {
                                        bannedAccountInRoomRepository.save(BannedAccountInRoomEntity(0, accountId, roomId))
                                    } else {
                                        Mono.empty()
                                    }
                                )
                        }
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

   //
   // private fun sendAccountAction(accountAction: AccountAction, accountEntity: AccountEntity) {
   //     simpMessagingTemplate.convertAndSend(
   //         "/topic/accounts",
   //         AccountActionDTO(accountAction, accountEntity.id, accountEntity.name)
   //     )
   // }
}