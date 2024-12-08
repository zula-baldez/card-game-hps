package com.example.common.kafkaconnections

data class AccountRegisteredEvent(
    val accountId: Long,
    val success: Boolean
)
