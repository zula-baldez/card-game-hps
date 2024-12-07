package com.example.personalaccount

import com.example.personalaccount.exceptions.InvalidAvatarFileException
import com.example.personalaccount.service.AvatarsHandler
import com.example.personalaccount.service.AvatarsServiceClient
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.web.multipart.MultipartFile

class AvatarsHandlerTest {

    private val avatarsServiceClient: AvatarsServiceClient = mock(AvatarsServiceClient::class.java)
    private val avatarsHandler = AvatarsHandler(avatarsServiceClient)

    @Test
    fun `test handleFile with invalid file type`() {
        val invalidFile: MultipartFile = mock(MultipartFile::class.java).apply {
            `when`(contentType).thenReturn("text/plain")
        }

        assertThrows(InvalidAvatarFileException::class.java) {
            avatarsHandler.handleFile(1L, invalidFile)
        }
    }

    @Test
    fun `test handleFile with file too big`() {
        val largeFile: MultipartFile = mock(MultipartFile::class.java).apply {
            `when`(contentType).thenReturn("image/png")
            `when`(size).thenReturn(5 * 1024 * 1024) // 5 MB
        }

        assertThrows(InvalidAvatarFileException::class.java) {
            avatarsHandler.handleFile(1L, largeFile)
        }
    }

}