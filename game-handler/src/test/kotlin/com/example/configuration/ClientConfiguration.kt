package com.example.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class ClientConfiguration {
    @Bean
    @Primary
    fun personalAccountClient() {

    }
}