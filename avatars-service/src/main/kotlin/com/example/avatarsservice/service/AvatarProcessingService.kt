package com.example.avatarsservice.service

import com.example.common.dto.Avatar

interface AvatarProcessingService {
    fun processAvatar(accountId: Long, bytes: ByteArray): Avatar
}