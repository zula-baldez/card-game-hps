package com.example.common.client

import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.dto.personalaccout.UpdateAccountRoomRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "personal-account")
interface PersonalAccountClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["/accounts/{id}"])
    fun getAccountById(@PathVariable id: Long): AccountDto

    @RequestMapping(method = [RequestMethod.POST], value = ["/accounts"])
    fun createAccount(createAccountDto: CreateAccountDto, @RequestHeader("Authorization") authHeader: String)

    @RequestMapping(method = [RequestMethod.PUT], value = ["/accounts/{id}/room"])
    fun updateAccountRoom(@PathVariable id: Long, updateAccountRoomRequest: UpdateAccountRoomRequest)

    @RequestMapping(method = [RequestMethod.POST], value = ["/accounts/{id}/fine"])
    fun addFine(@PathVariable id: Long)
}