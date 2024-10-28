package com.example.roomservice.service

import com.example.common.dto.roomservice.AccountAction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoomAccountManager {
    fun addAccount(roomId: Long, accountId: Long): Mono<Void>
    fun removeAccount(roomId: Long, accountId: Long, reason: AccountAction): Mono<Void>
    fun getAccountRoom(accountId: Long): Mono<Long>
    fun getAccountsInRoom(roomId: Long): Flux<Long>
    fun getBannedAccountsInRoom(roomId: Long): Flux<Long>
}