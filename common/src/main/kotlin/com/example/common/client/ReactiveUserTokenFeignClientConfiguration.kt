package com.example.common.client

import com.example.common.interceptor.ReactiveUserTokenRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import reactivefeign.client.ReactiveHttpRequestInterceptor

class ReactiveUserTokenFeignClientConfiguration(
    @Autowired
    private val serviceTokenClient: ReactiveServiceTokenClient,
    @Value("\${spring.application.name}")
    private val serviceName: String
) {
    @Bean
    fun interceptor(): ReactiveHttpRequestInterceptor {
        return ReactiveUserTokenRequestInterceptor(serviceTokenClient, serviceName)
    }
}