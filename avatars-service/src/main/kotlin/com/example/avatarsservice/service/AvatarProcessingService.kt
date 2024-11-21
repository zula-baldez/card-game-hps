package com.example.avatarsservice.service

import com.example.common.dto.Avatar
import org.springframework.web.multipart.MultipartFile

interface AvatarProcessingService {
    fun processAvatar(accountId: Long, file: MultipartFile): Avatar
}