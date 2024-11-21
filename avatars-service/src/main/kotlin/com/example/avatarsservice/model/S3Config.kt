package com.example.avatarsservice.model

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("s3")
data class S3Config(
    val endpoint: String,
    val region: String,
    val bucket: String,
    val accessKeyId: String,
    val secretAccessKey: String
)