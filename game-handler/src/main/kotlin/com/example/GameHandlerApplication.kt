package com.example

import com.example.common.config.RsaKeyProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties::class)
@EnableFeignClients
@EnableReactiveFeignClients
class GameHandlerApplication

fun main(args: Array<String>) {
    runApplication<GameHandlerApplication>(*args)
}
