package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableReactiveFeignClients
class PersonalAccountApplication

fun main(args: Array<String>) {
    runApplication<PersonalAccountApplication>(*args)
}
