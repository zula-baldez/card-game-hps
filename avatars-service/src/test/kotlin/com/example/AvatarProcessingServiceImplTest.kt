package com.example

import com.example.avatarsservice.service.AvatarProcessingServiceImpl
import com.example.avatarsservice.service.AvatarsStorage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class AvatarProcessingServiceImplTest {

    private lateinit var storage: AvatarsStorage
    private lateinit var service: AvatarProcessingServiceImpl

    @BeforeEach
    fun setUp() {
        storage = mock(AvatarsStorage::class.java)
        service = AvatarProcessingServiceImpl(storage)
    }

    @Test
    fun `processAvatar should store avatar and return Avatar object`() {
        val accountId = 1L
        val originalImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(originalImage, "jpg", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val expectedUrl = "http://example.com/user-avatar-1-1234567890.jpg"
        `when`(storage.storeFile(anyString(), any())).thenReturn(expectedUrl)

        val result = service.processAvatar(accountId, imageBytes)

        assertEquals(accountId, result.accountId)
        assertEquals(expectedUrl, result.url)

        verify(storage).storeFile(startsWith("user-avatar-$accountId-"), any())
    }

    @Test
    fun `processAvatar should handle small images correctly`() {
        val accountId = 2L
        val originalImage = BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(originalImage, "jpg", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val expectedUrl = "http://example.com/user-avatar-2-1234567891.jpg"
        `when`(storage.storeFile(anyString(), any())).thenReturn(expectedUrl)

        val result = service.processAvatar(accountId, imageBytes)

        assertEquals(accountId, result.accountId)
        assertEquals(expectedUrl, result.url)

        verify(storage).storeFile(startsWith("user-avatar-$accountId-"), any())
    }

}
