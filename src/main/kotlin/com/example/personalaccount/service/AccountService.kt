package com.example.personalaccount.service

import com.example.personalaccount.database.Account

interface AccountService {
    fun findByIdOrThrow(id: Long): Account
}