package com.example

import com.example.common.RsaKeyProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties::class)
@EnableFeignClients
class PersonalAccountApplication

fun main(args: Array<String>) {
    runApplication<PersonalAccountApplication>(*args)
}
