package com.example.avatarsservice.controllers

import com.example.avatarsservice.exceptions.InvalidImageException
import com.example.common.dto.Avatar
import com.example.avatarsservice.service.AvatarProcessingService
import com.example.common.exceptions.AccountNotFoundException
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/avatars")
class AvatarsController(
    private val avatarProcessingService: AvatarProcessingService
) {

    @PostMapping("/{id}")
    @Operation(summary = "Process avatar for account")
    fun getAccountById(@PathVariable id: Long, file: MultipartFile): Avatar {
        if (file.contentType?.equals("image/jpeg") != true) {
            throw InvalidImageException()
        }

        val avatar = avatarProcessingService.processAvatar(id, file)
        return avatar
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
