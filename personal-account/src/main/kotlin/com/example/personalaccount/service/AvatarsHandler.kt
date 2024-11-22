package com.example.personalaccount.service

import com.example.common.dto.ProcessAvatarRequest
import com.example.personalaccount.exceptions.InvalidAvatarFileException
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Component
class AvatarsHandler(private val avatarsServiceClient: AvatarsServiceClient) {
    private val maxSize = 4 * 1024 * 1024
    private val expectedType = "image/"

    fun handleFile(accountId: Long, file: MultipartFile) {
        if (file.contentType?.startsWith(expectedType) != true) {
            throw InvalidAvatarFileException("Invalid file type")
        }

        if (file.size > maxSize) {
            throw InvalidAvatarFileException("File too big")
        }

        avatarsServiceClient.sendProcessAvatarRequest(ProcessAvatarRequest(
            accountId,
            Base64.getEncoder().encodeToString(file.bytes)
        ))
    }
}