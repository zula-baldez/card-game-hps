package com.example.authservice.kafkaconnections

import com.example.authservice.service.CreateAccountDtoSender
import com.example.common.dto.personalaccout.CreateAccountDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaCreateAccountDtoSender(private val template: KafkaTemplate<String, CreateAccountDto>) : CreateAccountDtoSender {
    override fun sendCreateAccount(createAccountDto: CreateAccountDto) {
        template.send("user-registration", createAccountDto)
    }
}