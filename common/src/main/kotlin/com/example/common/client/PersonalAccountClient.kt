package com.example.common.client

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(primary = false, name = "personal-account", configuration = [UserTokenFeignClientConfiguration::class])
interface PersonalAccountClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["/accounts/{id}"])
    fun getAccountById(@PathVariable id: Long): AccountDto

    @RequestMapping(method = [RequestMethod.POST], value = ["/accounts"])
    fun createAccount(createAccountDto: CreateAccountDto)

    @RequestMapping(method = [RequestMethod.PUT], value = ["/accounts/{id}/room"])
    fun updateAccountRoom(@PathVariable id: Long, updateAccountRoomRequest: UpdateAccountRoomRequest)

    @RequestMapping(method = [RequestMethod.POST], value = ["/accounts/{id}/fine"])
    fun addFine(@PathVariable id: Long)
}