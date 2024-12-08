package com.example.personalaccount.service

import com.example.common.kafkaconnections.AccountRegisteredEvent

interface AccountRegisteredEventSender {
    fun sendAccountRegisteredEvent(event: AccountRegisteredEvent)
}