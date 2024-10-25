package com.example.common.client

import com.example.common.dto.personalaccout.business.AccountDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(url = "http://localhost:8083", name = "personal-account")
interface PersonalAccountClient {

    @RequestMapping(method = [RequestMethod.POST], value = ["/accounts/{id}"])
    fun getAccountById(@PathVariable id: Long): AccountDto

    @RequestMapping(method = [RequestMethod.POST], value = ["/accounts/{id}/fine"])
    fun addFine(id: Long)
}