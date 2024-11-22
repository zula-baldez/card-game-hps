package com.example.personalaccount.service

import com.example.common.dto.ProcessAvatarRequest

interface AvatarsServiceClient {
    fun sendProcessAvatarRequest(request: ProcessAvatarRequest)
}