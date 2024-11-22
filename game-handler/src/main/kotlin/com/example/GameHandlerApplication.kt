package com.example

import com.example.common.config.RsaKeyProperties
import com.example.common.kafkaconnections.KafkaProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties::class, KafkaProperties::class)
@EnableReactiveFeignClients
@EnableFeignClients
class GameHandlerApplication

fun main(args: Array<String>) {
    runApplication<GameHandlerApplication>(*args)
}
