package com.example.common.client

import com.example.common.interceptor.UserTokenRequestInterceptor
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class UserTokenFeignClientConfiguration(
    @Autowired
    private val serviceTokenClient: ServiceTokenClient,
    @Value("\${spring.application.name}")
    private val serviceName: String
) {
    @Bean
    fun userTokenInterceptor(): RequestInterceptor {
        return UserTokenRequestInterceptor(serviceTokenClient, serviceName)
    }
}