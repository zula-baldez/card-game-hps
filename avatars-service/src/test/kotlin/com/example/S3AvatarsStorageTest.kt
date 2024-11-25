package com.example

import com.example.avatarsservice.exceptions.S3UploadException
import com.example.avatarsservice.model.S3Config
import com.example.avatarsservice.service.S3AvatarsStorage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

class S3AvatarsStorageTest {

    private val s3Config = S3Config(
        endpoint = "http://localhost:9000",
        bucket = "test-bucket",
        accessKeyId = "test-access-key",
        secretAccessKey = "test-secret-key",
        region = "us-east-1"
    )

    private var mockS3Client: S3Client = mock(S3Client::class.java)

    private var storage = S3AvatarsStorage(s3Config, mockS3Client)

    @Test
    fun `storeFile should store file successfully`() {
        val id = "avatar-id"
        val bytes = byteArrayOf(1, 2, 3)
        val expectedUrl = "${s3Config.endpoint}/${s3Config.bucket}/$id"
        val responseMock = PutObjectResponse.builder()
            .sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build())
            .build()

        `when`(mockS3Client.putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java)))
            .thenReturn(responseMock as PutObjectResponse?)

        // Act
        val result = storage.storeFile(id, bytes)

        // Assert
        assertEquals(expectedUrl, result)

        val requestCaptor = ArgumentCaptor.forClass(PutObjectRequest::class.java)
        verify(mockS3Client).putObject(requestCaptor.capture(), any(RequestBody::class.java))
        val capturedRequest = requestCaptor.value

        assertEquals(s3Config.bucket, capturedRequest.bucket())
        assertEquals(id, capturedRequest.key())
        assertEquals("image/jpeg", capturedRequest.contentType())
    }

    @Test
    fun `storeFile should throw S3UploadException on failure`() {
        // Arrange
        val id = "avatar-id"
        val bytes = byteArrayOf(1, 2, 3)
        val responseMock = PutObjectResponse.builder()
            .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
            .build()

        `when`(mockS3Client.putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java)))
            .thenReturn(responseMock as PutObjectResponse?)

        // Act & Assert
        assertThrows(S3UploadException::class.java) {
            storage.storeFile(id, bytes)
        }

        verify(mockS3Client).putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java))
    }

    @Test
    fun `storeFile should throw exception if S3 client throws`() {
        // Arrange
        val id = "avatar-id"
        val bytes = byteArrayOf(1, 2, 3)

        `when`(mockS3Client.putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java)))
            .thenThrow(RuntimeException("S3 is down"))

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            storage.storeFile(id, bytes)
        }

        verify(mockS3Client).putObject(any(PutObjectRequest::class.java), any(RequestBody::class.java))
    }
}
