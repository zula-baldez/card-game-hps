package com.example.personalaccount.service

import com.example.common.kafkaconnections.AccountRegisteredEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaAccountRegisteredEventSender(private val kafkaTemplate: KafkaTemplate<String, AccountRegisteredEvent>) :
    AccountRegisteredEventSender {
    override fun sendAccountRegisteredEvent(event: AccountRegisteredEvent) {
        kafkaTemplate.send("account-creation", event)
    }
}