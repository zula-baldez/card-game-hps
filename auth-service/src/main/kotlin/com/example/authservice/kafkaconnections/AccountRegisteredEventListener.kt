package com.example.authservice.kafkaconnections

import com.example.authservice.service.RegistrationService
import com.example.common.kafkaconnections.AccountRegisteredEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class AccountRegisteredEventListener(
    private val registrationService: RegistrationService
) {
    @KafkaListener(topics = ["account-creation"])
    fun handleAccountCreation(event: AccountRegisteredEvent) {
        if (event.success) {
            registrationService.commitRegistration(event.accountId)
        } else {
            registrationService.rollbackRegistration(event.accountId)
        }
    }
}