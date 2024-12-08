package com.example.authservice.service

import com.example.common.dto.personalaccout.CreateAccountDto

interface CreateAccountDtoSender {
    fun sendCreateAccount(createAccountDto: CreateAccountDto)
}