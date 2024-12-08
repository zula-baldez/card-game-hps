package com.example.personalaccount.service

import com.example.common.dto.personalaccout.CreateAccountDto
import com.example.common.kafkaconnections.AccountRegisteredEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class CreateAccountDtoListener(
    private val accountService: AccountService,
    private val sender: AccountRegisteredEventSender
) {
    @KafkaListener(topics = ["user-registration"])
    fun handleCreateAccountDto(event: CreateAccountDto) {
        try {
            accountService.createAccountForUser(event)
            sender.sendAccountRegisteredEvent(
                AccountRegisteredEvent(
                    event.id,
                    success = true
                )
            )
        } catch (e: Exception) {
            sender.sendAccountRegisteredEvent(
                AccountRegisteredEvent(
                    event.id,
                    success = false
                )
            )
        }
    }
}