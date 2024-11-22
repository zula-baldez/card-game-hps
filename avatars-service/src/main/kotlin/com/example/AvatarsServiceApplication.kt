package com.example

import com.example.common.kafkaconnections.KafkaProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableReactiveFeignClients
@EnableConfigurationProperties(KafkaProperties::class)
class AvatarsServiceApplication

fun main(args: Array<String>) {
    runApplication<AvatarsServiceApplication>(*args)
}
