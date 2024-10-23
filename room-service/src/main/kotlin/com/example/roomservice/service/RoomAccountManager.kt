package com.example.roomservice.service

import com.example.common.dto.business.AccountAction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoomAccountManager {
    fun addAccount(roomId: Long, accountId: Long, requesterId: Long): Mono<Void>
    fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction, requesterId: Long): Mono<Void>
    fun getAccountRoom(accountId: Long): Mono<Long>
    fun getAccountsInRoom(roomId: Long): Flux<Long>
    fun getBannedAccountsInRoom(roomId: Long): Flux<Long>
}