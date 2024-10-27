package com.example.authservice.jwt

import com.example.authservice.database.UserEntity
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors


@Service
class TokenService(private val encoder: JwtEncoder) {
    private val expireTimeMinutes = 100L;

    fun generateAccessToken(userEntity: UserEntity, serviceName: String): String {
        val now = Instant.now()
        val scope: String = userEntity.roles.stream()
            .map { obj -> obj?.roleName.toString() }
            .collect(Collectors.joining(" "))
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(expireTimeMinutes, ChronoUnit.MINUTES))
            .subject(userEntity.id.toString())
            .claim("scope", scope)
            .claim("name", userEntity.name)
            .claim("service", serviceName)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}