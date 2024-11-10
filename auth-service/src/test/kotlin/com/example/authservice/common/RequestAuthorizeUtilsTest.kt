package com.example.authservice.common

import com.example.common.security.RequestAuthorizeUtils
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant


class RequestAuthorizeUtilsTest {

    private val requestAuthorizeUtils = RequestAuthorizeUtils()

    @Test
    fun `jwtClaimEquals should return true for matching claims`() {
        val claimKey = "testClaim"
        val claimValue = "testValue"

        val signedJWT = Jwt(
            "testToken",
            Instant.now(),
            Instant.MAX,
            mapOf(Pair(claimKey, claimValue)),
            mapOf(Pair(claimKey, claimValue))
        )

        val result = requestAuthorizeUtils.jwtClaimEquals(signedJWT, claimKey, claimValue)

        assertTrue(result)
    }

    @Test
    fun `jwtClaimEquals should return false for non-matching claims`() {
        val claimKey = "testClaim"
        val claimValue = "wrongValue"
        val claims = mapOf(claimKey to "testValue")
        val signedJWT = Jwt(
            "testToken",
            Instant.now(),
            Instant.MAX,
            mapOf(Pair(claimKey, claimValue)),
            claims
        )

        val result = requestAuthorizeUtils.jwtClaimEquals(signedJWT, claimKey, claimValue)

        assertFalse(result)
    }

    @Test
    fun `jwtClaimEquals should return false when principal is not Jwt`() {
        val result = requestAuthorizeUtils.jwtClaimEquals("notAJwt", "testClaim", "value")

        assertFalse(result)
    }

    @Test
    fun `test should return true`() {
        val result = requestAuthorizeUtils.test()
        assertTrue(result)
    }
}