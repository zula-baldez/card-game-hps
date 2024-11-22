package com.example.avatarsservice.controllers

import com.example.avatarsservice.exceptions.InvalidImageException
import com.example.avatarsservice.service.AvatarProcessingService
import com.example.avatarsservice.service.AvatarResultProducer
import com.example.common.dto.ProcessAvatarRequest
import com.example.common.exceptions.AccountNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class AvatarsController(
    private val avatarProcessingService: AvatarProcessingService,
    private val avatarResultProducer: AvatarResultProducer
) {

    @MessageMapping("/process-avatar")
    fun processAvatar(request: ProcessAvatarRequest) {
        val avatar =
            avatarProcessingService.processAvatar(
                request.accountId,
                Base64.getDecoder().decode(request.encodedImage)
            )
        avatarResultProducer.sendResult(avatar)
    }

    @ExceptionHandler(InvalidImageException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidImage(ex: InvalidImageException): String {
        return ex.message ?: "Image is invalid"
    }

    @ExceptionHandler(AccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleAccountNotFound(ex: AccountNotFoundException): String {
        return ex.message ?: "Account not found"
    }
}
