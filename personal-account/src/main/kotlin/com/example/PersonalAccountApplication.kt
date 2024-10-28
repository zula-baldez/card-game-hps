package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableReactiveFeignClients
class PersonalAccountApplication

fun main(args: Array<String>) {
    runApplication<PersonalAccountApplication>(*args)
}
