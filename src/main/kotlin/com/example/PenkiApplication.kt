package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PenkiApplication

fun main(args: Array<String>) {
    runApplication<PenkiApplication>(*args)
}
