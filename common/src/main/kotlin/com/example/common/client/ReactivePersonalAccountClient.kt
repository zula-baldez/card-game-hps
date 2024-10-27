package com.example.common.client

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient("personal-account", configuration = [ReactiveUserTokenFeignClientConfiguration::class])
interface ReactivePersonalAccountClient {
    @RequestMapping(method = [RequestMethod.GET], value = ["/accounts/{id}"])
    fun getAccountById(@PathVariable id: Long): Mono<AccountDto>

    @RequestMapping(method = [RequestMethod.POST], value = ["/accounts"])
    fun createAccount(createAccountDto: CreateAccountDto): Mono<Void>

    @RequestMapping(method = [RequestMethod.PUT], value = ["/accounts/{id}/room"])
    fun updateAccountRoom(@PathVariable id: Long, updateAccountRoomRequest: UpdateAccountRoomRequest): Mono<AccountDto>

    @RequestMapping(method = [RequestMethod.POST], value = ["/accounts/{id}/fine"])
    fun addFine(@PathVariable id: Long): Mono<Void>
}