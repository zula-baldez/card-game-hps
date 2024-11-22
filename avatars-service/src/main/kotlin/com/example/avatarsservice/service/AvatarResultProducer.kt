package com.example.avatarsservice.service

import com.example.common.dto.Avatar

interface AvatarResultProducer {
    fun sendResult(avatar: Avatar)
}