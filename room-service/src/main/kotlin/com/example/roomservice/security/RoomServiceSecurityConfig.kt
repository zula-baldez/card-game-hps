package com.example.roomservice.security

import com.example.common.config.RsaKeyProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer



@Configuration
@EnableConfigurationProperties(RsaKeyProperties::class)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class RoomServiceSecurityConfig(
    val rsaKeyProperties: RsaKeyProperties
) {
    @Bean
    @Throws(Exception::class)
    fun roomServiceFilterChain(
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain {
        http
            .authorizeExchange {
                it.pathMatchers(
                    "/v3/api-docs*/**", "/swagger-ui/**"
                ).permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer {
                it.jwt { jwt ->
                    jwt.jwtDecoder(reactiveJwtDecoder())
                }
            }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
        return http.build()
    }

    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder {
        return NimbusReactiveJwtDecoder.withPublicKey(rsaKeyProperties.publicKey).build()
    }

}