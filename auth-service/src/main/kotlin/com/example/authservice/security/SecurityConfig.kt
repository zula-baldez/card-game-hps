package com.example.authservice.security

import com.example.common.config.RsaKeyProperties
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    val rsaKeyProperties: RsaKeyProperties,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain {
        http
            .authorizeExchange {
                it.pathMatchers("/auth/login").permitAll()
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

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(rsaKeyProperties.publicKey).privateKey(rsaKeyProperties.privateKey).build()
        val jwks = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey).build()
    }
}
