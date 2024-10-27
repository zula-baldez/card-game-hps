package com.example.personalaccount.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
//            .oauth2ResourceServer { oauth2ResourceServer ->
//                oauth2ResourceServer.jwt { token ->
//                    token.decoder(jwtDecoder())
//                }
//            }.build()
            .build()
    }

//    @Bean
//    fun jwtDecoder(): JwtDecoder {
//        return NimbusJwtDecoder.withPublicKey("rsaKeyProperties.publicKey").build()
//    }
}