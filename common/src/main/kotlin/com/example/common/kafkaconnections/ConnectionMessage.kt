package com.example.common.kafkaconnections

import com.example.common.dto.personalaccout.AccountDto

data class ConnectionMessage(
    val type: ConnectionMessageType,
    val roomId: Long,
    val accountDto: AccountDto
)
