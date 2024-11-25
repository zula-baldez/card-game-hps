package com.example.configuration

import com.example.common.client.PersonalAccountClient
import com.example.common.client.RoomServiceClient
import com.example.common.dto.personalaccout.AccountDto
import com.example.common.dto.roomservice.RoomDto
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TestConfiguration {
    @Bean
    @Primary
    fun personalAccountClient(): PersonalAccountClient {
        val mock = mock(PersonalAccountClient::class.java)
        lenient().`when`(mock.getAccountById(6)).thenReturn(
            AccountDto(6, "name", 0, "avatar", 6)
        )
        lenient().`when`(mock.getAccountById(7)).thenReturn(
            AccountDto(7, "name", 0, "avatar", 7)
        )
        lenient().`when`(mock.getAccountById(8)).thenReturn(
            AccountDto(8, "name", 0, "avatar", 8)
        )
        return mock

    }

    @Bean
    @Primary
    fun roomServiceClient(): RoomServiceClient {
        var mock = mock(RoomServiceClient::class.java)
        lenient().`when`(mock.findById(1)).thenReturn(
            RoomDto(
                1,
                "name",
                6,
                3,
                listOf(
                    AccountDto(6, "name", 0, "avatar", 1),
                    AccountDto(7, "name", 0,"avatar",  1),
                    AccountDto(8, "name", 0, "avatar", 1)
                ),
                1
            )
        )
        return mock
    }

}