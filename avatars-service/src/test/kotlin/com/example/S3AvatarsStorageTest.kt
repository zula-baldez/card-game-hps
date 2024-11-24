package com.example

import com.example.avatarsservice.exceptions.S3UploadException
import com.example.avatarsservice.model.S3Config
import com.example.avatarsservice.service.S3AvatarsStorage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client


class S3AvatarsStorageTest {

    private val s3Config = mock(S3Config::class.java)
    private val s3Client = mock(S3Client::class.java)
    private val storage = S3AvatarsStorage(s3Config)

    init {
        `when`(s3Config.region).thenReturn("us-west-1")
        `when`(s3Config.endpoint).thenReturn("https://s3.example.com")
        `when`(s3Config.bucket).thenReturn("my-bucket")
        `when`(s3Config.accessKeyId).thenReturn("access-key-id")
        `when`(s3Config.secretAccessKey).thenReturn("secret-access-key")

        `when`(S3Client.builder()).thenReturn(mock(S3Client.builder()::class.java))
        `when`(s3Client.putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java)))
            .thenReturn(PutObjectResponse.builder().build())
    }

    @Test
    fun `test storeFile success`() {
        val id = "avatar123"
        val bytes = byteArrayOf(1, 2, 3)

        val url = storage.storeFile(id, bytes)

        val requestCaptor = argumentCaptor<PutObjectRequest>()
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody::class.java))

        assertEquals("https://s3.example.com/my-bucket/$id", url)

        val capturedRequest = requestCaptor.firstValue
        assertEquals("my-bucket", capturedRequest.bucket())
        assertEquals(id, capturedRequest.key())
        assertEquals("image/jpeg", capturedRequest.contentType())
    }

    @Test
    fun `test storeFile failure`() {
        val id = "avatar123"
        val bytes = byteArrayOf(1, 2, 3)

        `when`(s3Client.putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java)))
            .thenThrow(RuntimeException("S3 upload failed"))

        assertThrows<S3UploadException> {
            storage.storeFile(id, bytes)
        }
    }

}