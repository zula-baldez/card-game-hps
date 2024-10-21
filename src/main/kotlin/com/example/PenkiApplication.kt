package com.example

import com.example.authservice.config.RsaKeyProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties::class)
class PenkiApplication

fun main(args: Array<String>) {
    runApplication<PenkiApplication>(*args)
}
