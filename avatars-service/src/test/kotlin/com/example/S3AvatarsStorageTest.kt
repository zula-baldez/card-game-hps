package com.example

import com.example.avatarsservice.exceptions.S3UploadException
import com.example.avatarsservice.model.S3Config
import com.example.avatarsservice.service.S3AvatarsStorage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.CreateBucketResponse
import software.amazon.awssdk.services.s3.model.S3Exception
import java.net.URI
import java.util.concurrent.CompletableFuture


class S3AvatarsStorageTest {

    private lateinit var s3Config: S3Config
    private lateinit var s3AvatarsStorage: S3AvatarsStorage
    private lateinit var s3Client: S3AsyncClient
    var host = minioContainer.host
    var port = minioContainer.getMappedPort(9000)

    companion object {
        private val minioContainer = GenericContainer(DockerImageName.parse("minio/minio:latest"))
            .withExposedPorts(9000)
            .withCommand("server /data")
            .withEnv("MINIO_ACCESS_KEY", "test-access-key")
            .withEnv("MINIO_SECRET_KEY", "test-secret-key")

        init {
            minioContainer.start()
        }
    }

    @BeforeEach
    fun setUp() {
        val host = minioContainer.host
        val port = minioContainer.getMappedPort(9000)
        s3Config = S3Config(
            region = "us-west-2",
            endpoint = "http://$host:$port",
            accessKeyId = "test-access-key",
            secretAccessKey = "test-secret-key",
            bucket = "test-bucket"
        )

        s3Client = S3AsyncClient.builder()
            .region(Region.of(s3Config.region))
            .endpointOverride(URI.create(s3Config.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(s3Config.accessKeyId, s3Config.secretAccessKey)
                )
            )
            .build()

        createBucket(s3Client, s3Config.bucket)
        s3AvatarsStorage = S3AvatarsStorage(s3Config)
    }

    @AfterEach
    fun tearDown() {
        val listObjectsResponse = s3Client.listObjects { it.bucket(s3Config.bucket) }
        s3Client.deleteBucket { it.bucket(s3Config.bucket) }
    }

    @Test
    fun `storeFile should upload file and return correct URL`() {
        val id = "avatar.jpg"
        val bytes = ByteArray(10) { 0 }

        val url = s3AvatarsStorage.storeFile(id, bytes)

        assertEquals("http://$host:$port/test-bucket/$id", url)
    }

    @Test
    fun `storeFile should throw S3UploadException on failure`() {
        val invalidS3Config = S3Config(
            region = "us-west-2",
            endpoint = "http://$host:$port",
            accessKeyId = "invalid-access-key",
            secretAccessKey = "invalid-secret-key",
            bucket = "test-bucket"
        )

        val invalidS3AvatarsStorage = S3AvatarsStorage(invalidS3Config)

        val id = "avatar.jpg"
        val bytes = ByteArray(10) { 0 }

        assertThrows<S3UploadException> {
            invalidS3AvatarsStorage.storeFile(id, bytes)
        }
    }

    fun createBucket(s3AsyncClient: S3AsyncClient, bucketName: String) {
        try {
            val bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build()

            val futureGet: CompletableFuture<CreateBucketResponse> = s3AsyncClient.createBucket(bucketRequest)
            futureGet.whenComplete { resp: CreateBucketResponse?, err: Throwable ->
                try {
                    if (resp != null) {
                        println("$bucketName is ready~")
                    } else {
                        err.printStackTrace()
                    }
                } finally {
                    s3AsyncClient.close()
                }
            }
            futureGet.join()
        } catch (e: S3Exception) {
            System.err.println(e.awsErrorDetails().errorMessage())
            System.exit(1)
        }
    }
}