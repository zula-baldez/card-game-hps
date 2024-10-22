package com.example

import com.example.common.RsaKeyProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties::class)
class GameHandlerApplication

fun main(args: Array<String>) {
    runApplication<GameHandlerApplication>(*args)
}
