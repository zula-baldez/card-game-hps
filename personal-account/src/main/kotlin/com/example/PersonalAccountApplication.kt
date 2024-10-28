package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PersonalAccountApplication

fun main(args: Array<String>) {
    runApplication<PersonalAccountApplication>(*args)
}
