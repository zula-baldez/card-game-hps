package com.example

import com.example.avatarsservice.controllers.AvatarsController
import com.example.avatarsservice.exceptions.InvalidImageException
import com.example.avatarsservice.service.AvatarProcessingService
import com.example.avatarsservice.service.AvatarResultProducer
import com.example.common.dto.Avatar
import com.example.common.dto.ProcessAvatarRequest
import com.example.common.exceptions.AccountNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import java.util.*

class AvatarsControllerTest {

    private val avatarProcessingService: AvatarProcessingService = mock(AvatarProcessingService::class.java)
    private val avatarResultProducer: AvatarResultProducer = mock(AvatarResultProducer::class.java)
    private val controller = AvatarsController(avatarProcessingService, avatarResultProducer)

    @Test
    fun `should process avatar and send result`() {
        val encodedImage = Base64.getEncoder().encodeToString("test-image".toByteArray())
        val request = ProcessAvatarRequest(1L, encodedImage)
        val processedAvatar = Avatar(1L, "test-image")

        `when`(avatarProcessingService.processAvatar(1L, Base64.getDecoder().decode(encodedImage)))
            .thenReturn(processedAvatar)

        controller.processAvatar(request)

        verify(avatarProcessingService).processAvatar(1L, Base64.getDecoder().decode(encodedImage))
        verify(avatarResultProducer).sendResult(processedAvatar)
    }

    @Test
    fun `should handle InvalidImageException`() {
        val exception = InvalidImageException()

        val response = controller.handleInvalidImage(exception)

        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST)
        assertEquals("Invalid file", response)
    }

    @Test
    fun `should handle AccountNotFoundException`() {
        val exceptionMessage = "Account with id 11 not found"
        val exception = AccountNotFoundException(11L)

        val response = controller.handleAccountNotFound(exception)
        assertEquals(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND)
        assertEquals(exceptionMessage, response)
    }
}