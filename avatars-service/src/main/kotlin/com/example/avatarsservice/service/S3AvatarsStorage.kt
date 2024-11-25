package com.example.avatarsservice.service

import com.example.avatarsservice.exceptions.S3UploadException
import com.example.avatarsservice.model.S3Config
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI

@Component
class S3AvatarsStorage(
    private val s3Config: S3Config,
    private val s3Client: S3Client = S3Client
        .builder()
        .region(Region.of(s3Config.region))
        .endpointOverride(URI.create(s3Config.endpoint))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.builder()
                    .accessKeyId(s3Config.accessKeyId)
                    .secretAccessKey(s3Config.secretAccessKey)
                    .build()
            )
        )
        .build()


) : AvatarsStorage {

    private fun buildUrl(id: String): String {
        return "${s3Config.endpoint}/${s3Config.bucket}/$id"
    }

    override fun storeFile(id: String, bytes: ByteArray): String {
        val response = s3Client.putObject(
            PutObjectRequest
                .builder()
                .bucket(s3Config.bucket)
                .contentType("image/jpeg")
                .key(id)
                .build(),
            RequestBody.fromBytes(bytes)
        )

        if (!response.sdkHttpResponse().isSuccessful) {
            throw S3UploadException()
        }

        return buildUrl(id)
    }
}