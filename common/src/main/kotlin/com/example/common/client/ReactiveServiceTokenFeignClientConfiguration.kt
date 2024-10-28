package com.example.common.client

import com.example.common.config.RobotCredentialsProperties
import com.example.common.interceptor.ReactiveServiceTokenRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactivefeign.client.ReactiveHttpRequestInterceptor

@Configuration
class ReactiveServiceTokenFeignClientConfiguration(
    @Autowired
    private val loginServiceClient: ReactiveLoginServiceClient,
    @Autowired
    private val robotCredentials: RobotCredentialsProperties
) {
    @Bean
    @Qualifier("service-token-interceptor")
    fun serviceTokenInterceptor(): ReactiveHttpRequestInterceptor {
        return ReactiveServiceTokenRequestInterceptor(loginServiceClient, robotCredentials)
    }
}