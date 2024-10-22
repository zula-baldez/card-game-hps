package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties::class)
class PersonalAccountApplication

fun main(args: Array<String>) {
    runApplication<PersonalAccountApplication>(*args)
}
