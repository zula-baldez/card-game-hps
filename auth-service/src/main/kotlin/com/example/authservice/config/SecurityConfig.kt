package com.example.authservice.config

import com.example.common.config.RsaKeyProperties
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val rsaKeyProperties: RsaKeyProperties,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authServiceFilterChain(
        http: HttpSecurity,
        successLoginPasswordHandler: SuccessLoginPasswordHandler,
    ): SecurityFilterChain {

        http
            .sessionManagement { i -> i.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { i ->
                i.requestMatchers(
                    AntPathRequestMatcher("/auth/register"),
                    AntPathRequestMatcher("/auth/login"),
                    AntPathRequestMatcher("/auth/service-token") // TODO authenticate service credentials
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .csrf { it.disable() }
            .formLogin { it.disable()}
        return http.build()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(rsaKeyProperties.publicKey).privateKey(rsaKeyProperties.privateKey).build()
        val jwks = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }
}
