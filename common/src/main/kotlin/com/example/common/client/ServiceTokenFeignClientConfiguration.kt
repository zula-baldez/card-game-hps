package com.example.common.client

import com.example.common.config.RobotCredentialsProperties
import com.example.common.interceptor.ServiceTokenRequestInterceptor
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean

class ServiceTokenFeignClientConfiguration(
    @Autowired
    private val loginServiceClient: LoginServiceClient,
    @Autowired
    private val robotCredentials: RobotCredentialsProperties
) {
    @Bean
    fun interceptor(): RequestInterceptor {
        return ServiceTokenRequestInterceptor(loginServiceClient, robotCredentials)
    }
}