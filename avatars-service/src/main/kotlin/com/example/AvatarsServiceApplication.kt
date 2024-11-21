package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableReactiveFeignClients
class AvatarsServiceApplication

fun main(args: Array<String>) {
    runApplication<AvatarsServiceApplication>(*args)
}
