package com.example.authservice.jwt

import com.example.authservice.database.User
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.text.ParseException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors


@Service
class TokenService(private val encoder: JwtEncoder) {
    private val expireTimeMinutes = 100L;

    fun generateAccessToken(user: User): String? {
        val now = Instant.now()
        val scope: String = user.roles.stream()
            .map { obj -> obj?.roleName.toString() }
            .collect(Collectors.joining(" "))
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(expireTimeMinutes, ChronoUnit.MINUTES))
            .subject(user.id.toString())
            .claim("scope", scope)
            .claim("name", user.name)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}