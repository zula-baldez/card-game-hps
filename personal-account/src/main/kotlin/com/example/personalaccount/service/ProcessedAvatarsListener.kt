package com.example.personalaccount.service

import com.example.common.dto.Avatar
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ProcessedAvatarsListener(private val accountsService: AccountService) {
    @KafkaListener(topics = ["processed-avatars"])
    fun listen(avatar: Avatar) {
        accountsService.updateAccountAvatar(avatar)
    }
}