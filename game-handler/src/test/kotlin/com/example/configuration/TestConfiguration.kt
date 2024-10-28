package com.example.configuration

import com.example.common.client.PersonalAccountClient
import com.example.common.client.RoomServiceClient
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TestConfiguration {
    @Bean
    @Primary
    fun personalAccountClient(): PersonalAccountClient {
        return mock(PersonalAccountClient::class.java)
    }

    @Bean
    @Primary
    fun roomServiceClient(): RoomServiceClient {
        return mock(RoomServiceClient::class.java)
    }


}