package com.example.common.client

import com.example.common.config.RobotCredentialsProperties
import com.example.common.interceptor.ReactiveServiceTokenRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import reactivefeign.client.ReactiveHttpRequestInterceptor

class ReactiveServiceTokenFeignClientConfiguration(
    @Autowired
    private val loginServiceClient: ReactiveLoginServiceClient,
    @Autowired
    private val robotCredentials: RobotCredentialsProperties
) {
    fun interceptor(): ReactiveHttpRequestInterceptor {
        return ReactiveServiceTokenRequestInterceptor(loginServiceClient, robotCredentials)
    }
}