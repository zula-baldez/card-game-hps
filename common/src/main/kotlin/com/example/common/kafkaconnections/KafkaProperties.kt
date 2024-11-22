package com.example.common.kafkaconnections

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("kafka")
data class KafkaProperties(
    val bootstrapServers: String
)
